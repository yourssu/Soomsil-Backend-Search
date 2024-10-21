package com.yourssu.search.crawling.utils

import com.yourssu.search.crawling.domain.Information
import com.yourssu.search.crawling.domain.InformationUrl
import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.repository.InformationRepository
import com.yourssu.search.crawling.repository.InformationUrlRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.FileNotFoundException
import java.time.LocalDate
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.regex.Pattern

@Component
class CrawlingUtils(
    private val informationRepository: InformationRepository,
    private val informationUrlRepository: InformationUrlRepository,

    @Value("\${general.user-agent}")
    private val userAgent: String,
    
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val mutex: Mutex = Mutex()
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun filteringToSaveDocuments(
        jobs: List<List<Element>>,
        sourceType: SourceType,
        urlSelector: String
    ): List<Element> {
        val savedData: List<InformationUrl>
        withContext(Dispatchers.IO) {
            savedData = informationUrlRepository.findAllBySourceType(sourceType)
        }
        val savedUrls = savedData.map { it.contentUrl }

        val temp = jobs.flatten().filterNot { element ->
            (element.selectFirst(urlSelector)?.attr("abs:href") ?: "") in savedUrls
        }

        return temp
    }

    suspend fun crawlingList(
        baseUrl: String,
        ulSelector: String,
        maxConcurrentRequests: Int = 10
    ): List<List<Element>> = coroutineScope {
        val resultList = mutableListOf<List<Element>>()
        val isFinished = AtomicBoolean(false)
        val pageNumber = AtomicInteger(1)

        val jobs = List(maxConcurrentRequests) {
            coroutineScope.launch {
                while (!isFinished.get()) {
                    val currentPage = pageNumber.getAndIncrement()
                    log.info("URL collecting.. pageNum: {}", currentPage)
                    try {
                        val elements = fetchPage(baseUrl, currentPage, ulSelector)
                        if (elements.isNotEmpty()) {
                            mutex.withLock {
                                resultList.add(elements)
                            }
                        } else {
                            isFinished.set(true)
                        }
                    } catch (e: Exception) {
                        when (e) {
                            is FileNotFoundException -> isFinished.set(true) // 문서가 없을 떄
                            else -> println("Error crawling page $currentPage: ${e.message}")
                        }
                    }
                }
            }
        }
    
        jobs.joinAll()
        log.info("URL collecting finished")
        resultList
    }

    private suspend fun fetchPage(baseUrl: String, pageNumber: Int, ulSelector: String): List<Element> = withContext(Dispatchers.IO) {
        val document = Jsoup.connect("$baseUrl/$pageNumber")
            .userAgent(userAgent)
            .get()
        val contents: List<Element> = document.select(ulSelector).map { it }
        
        if (contents.isEmpty()) {
            throw FileNotFoundException("No more pages")
        }

        val firstElement: Element = contents[0]
        
        if (firstElement.hasClass("empty")) {
            throw FileNotFoundException("No more pages")
        }

        contents
    }

    suspend fun crawlingContents(
        toSaveDocuments: List<Element>,
        titleSelector: String,
        contentSelector: String,
        urlSelector: String,
        dateSelector: String,
        favicon: String?,
        source: String,
        sourceType: SourceType
    ) {
        val newUrls = mutableListOf<InformationUrl>()

        val contentJobs: List<Job> = toSaveDocuments.map { element ->
            coroutineScope.launch {
                val rawDate = element.selectFirst(dateSelector)?.text() ?: ""
                val title = element.selectFirst(titleSelector)?.text() ?: ""
                val contentUrl = element.selectFirst(urlSelector)?.attr("abs:href") ?: ""
                val paragraphs = Jsoup.connect(contentUrl).get().select(contentSelector)

                val imgList = paragraphs.select("img").map { img -> img.attr("src") }

                val content = StringBuilder()
                for (paragraph in paragraphs) {
                    val trimmedText = paragraph.text().replace("\\s+".toRegex(), " ").trim()
                    if (trimmedText.isNotEmpty()) {
                        content.append(trimmedText).append("\n")
                    }
                }
                val extractedDate = extractDate(rawDate)

                if (extractedDate == null) {
                    log.error("날짜 추출 실패 : $rawDate")
                    return@launch
                }

                // FIXME: 트랜잭션 처리 필요
                mutex.withLock {
                    newUrls.add(InformationUrl(contentUrl = contentUrl, sourceType = sourceType))

                    informationRepository.save(
                        Information(
                            title = title,
                            content = content.toString().trim(),
                            date = extractedDate,
                            contentUrl = contentUrl,
                            imgList = imgList,
                            favicon = favicon,
                            source = source
                        )
                    )
                }
            }
        }

        contentJobs.joinAll()
        val toSaveUrls: List<InformationUrl> = newUrls.distinctBy { it.contentUrl }
        informationUrlRepository.saveAll(toSaveUrls)
    }

    private fun extractDate(dateStr: String): LocalDate? {
        val datePattern = Pattern.compile("(\\d{4})\\.(\\d{2})\\.(\\d{2})")
        val matcher = datePattern.matcher(dateStr)

        return if (matcher.find()) {
            val year = matcher.group(1).toInt()
            val month = matcher.group(2).toInt()
            val day = matcher.group(3).toInt()

            try {
                LocalDate.of(year, month, day)
            } catch (e: Exception) {
                null // 날짜 변환 실패 시 null 반환
            }
        } else {
            null // 정규표현식에 맞지 않으면 null 반환
        }
    }
}