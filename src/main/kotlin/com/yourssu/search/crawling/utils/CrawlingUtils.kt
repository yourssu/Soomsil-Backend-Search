package com.yourssu.search.crawling.utils

import com.yourssu.search.crawling.domain.Information
import com.yourssu.search.crawling.domain.InformationUrl
import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.repository.InformationRepository
import com.yourssu.search.crawling.repository.InformationUrlRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.regex.Pattern

@Component
class CrawlingUtils(
    private val informationRepository: InformationRepository,
    private val informationUrlRepository: InformationUrlRepository,

    @Value("\${general.user-agent}")
    private val userAgent: String
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    private suspend fun getFavicon(baseUrl: String): String? {
        val document = Jsoup.connect(baseUrl)
            .userAgent(userAgent)
            .get()

        var faviconElement: Element? = document.head()
            .select("link[href~=.*\\.ico]")
            .first()

        return if (faviconElement != null) {
            faviconElement.attr("href")
        } else {
            faviconElement = document.head()
                .select("link[rel=icon]")
                .first()

            faviconElement?.attr("href")
        }
    }

    suspend fun filteringAlreadySavedData(
        jobs: List<Deferred<List<Element>>>,
        sourceType: SourceType,
        urlSelector: String
    ): List<Element> {
        val savedData: List<InformationUrl>
        withContext(Dispatchers.IO) {
            savedData = informationUrlRepository.findAllBySourceType(sourceType)
        }
        val savedUrls = savedData.map { it.contentUrl }

        val temp = jobs.awaitAll().flatten().filterNot { element ->
            (element.selectFirst(urlSelector)?.attr("abs:href") ?: "") in savedUrls
        }

        return temp
    }

    suspend fun crawlingList(
        baseUrl: String,
        ulSelector: String,
        endNumber: Int
    ): List<Deferred<List<Element>>> {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val jobs = mutableListOf<Deferred<List<Element>>>()

        for (pageNumber in 1..endNumber) {
            val deferredJob: Deferred<List<Element>> = coroutineScope.async {
                log.info("crawling page number : {}", pageNumber)
                val document = Jsoup.connect("$baseUrl/$pageNumber")
                    .userAgent(userAgent)
                    .get()

                document.select(ulSelector).toList()
            }
            jobs.add(deferredJob)
        }
        return jobs
    }

    suspend fun crawlingContents(
        jobs: List<Element>,
        titleSelector: String,
        contentSelector: String,
        urlSelector: String,
        dateSelector: String,
        favicon: String?,
        source: String,
        sourceType: SourceType
    ) {
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        val newUrls = mutableListOf<InformationUrl>()

        val contentJobs = jobs.map { deferredList ->
            coroutineScope.async {
                val rawDate = deferredList.selectFirst(dateSelector)?.text() ?: ""
                val title = deferredList.selectFirst(titleSelector)?.text() ?: ""
                val contentUrl = deferredList.selectFirst(urlSelector)?.attr("abs:href") ?: ""
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

                if (extractedDate != null) {
                    synchronized(newUrls) {
                        if (newUrls.none { it.contentUrl == contentUrl }) {
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
                } else {
                    log.error("날짜 추출 실패 : $rawDate")
                }
            }
        }

        contentJobs.awaitAll()
        informationUrlRepository.saveAll(newUrls)
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