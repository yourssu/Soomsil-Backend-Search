package com.yourssu.search.crawling.utils

import com.yourssu.search.crawling.domain.Information
import com.yourssu.search.crawling.domain.InformationUrl
import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.repository.InformationRepository
import com.yourssu.search.crawling.repository.InformationUrlRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.FileNotFoundException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger

@Component
class CrawlingUtils(
    private val informationRepository: InformationRepository,
    private val informationUrlRepository: InformationUrlRepository,

    @Value("\${general.user-agent}")
    private val userAgent: String,

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO),
    private val dateExtractor: DateExtractor
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    suspend fun filteringToSaveDocuments(
        lists: List<List<Element>>,
        sourceType: SourceType,
        urlSelector: String,
    ): List<Element> {
        val savedData: List<InformationUrl>
        withContext(Dispatchers.IO) {
            savedData = informationUrlRepository.findAllBySourceType(sourceType)
        }
        val savedUrls = savedData.map { it.contentUrl }

        val temp = lists.flatten().filterNot { element ->
            (element.selectFirst(urlSelector)?.attr("abs:href") ?: "") in savedUrls
        }

        return temp
    }

    suspend fun crawlingList(
        baseUrl: String,
        ulSelector: String,
        maxConcurrentRequests: Int = 10,
        pageRequestType: PageRequestType = PageRequestType.PATH_PARAMETER,
        channelCapacity: Int
    ): List<List<Element>> = coroutineScope {
        val elementChannel = Channel<List<Element>>(channelCapacity)
        val isFinished = AtomicBoolean(false)
        val pageNumber = AtomicInteger(1)

        // 크롤링 데이터를 채널에 보내는 작업 (생산자)
        val producerJob = launch {
            val jobs = List(maxConcurrentRequests) {
                launch {
                    while (!isFinished.get()) {
                        val currentPage: Int = pageNumber.getAndIncrement()
                        log.info("URL collecting.. pageNum: {}", currentPage)
                        val pageUrl: String = getPageUrl(baseUrl, currentPage, pageRequestType)
                        try {
                            val elements: List<Element> = fetchPage(pageUrl, ulSelector)
                            if (elements.isNotEmpty()) {
                                elementChannel.send(elements)
                            } else {
                                isFinished.set(true) // 더 이상 데이터가 없으면 종료 플래그 설정
                            }
                        } catch (e: Exception) {
                            when (e) {
                                is FileNotFoundException -> isFinished.set(true)
                                else -> log.error("Error crawling page $currentPage: ${e.message}", e)
                            }
                        }
                    }
                }
            }
            jobs.joinAll()
            elementChannel.close()
        }

        // 채널에서 데이터를 소비하는 작업 (소비자)
        val resultList = mutableListOf<List<Element>>()
        val consumerJob = launch {
            for (element in elementChannel) {
                resultList.add(element)
            }
        }

        producerJob.join() // 생산자 작업 완료 대기
        consumerJob.join() // 소비자 작업 완료 대기

        log.info("URL collecting finished")
        resultList
    }
    
    private suspend fun getPageUrl(baseUrl: String, currentPage: Int, pageRequestType: PageRequestType): String {
        return when (pageRequestType) {
            PageRequestType.PATH_PARAMETER -> "$baseUrl$currentPage"
            PageRequestType.QUERY_PARAMETER -> "$baseUrl$currentPage"
        }
    }


    private suspend fun fetchPage(pageUrl: String, ulSelector: String): List<Element> {
        val document = Jsoup.connect(pageUrl)
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

        return contents
    }

    suspend fun crawlingContents(
        toSaveDocuments: List<Element>,
        titleSelector: String,
        contentSelector: String,
        urlSelector: String,
        dateSelector: String,
        favicon: String?,
        sourceType: SourceType,
        channelCapacity: Int = 10000,
        delayMillis: Long = 0L
    ) = coroutineScope {
        val urlChannel = Channel<InformationUrl>(channelCapacity)

        // 생산자: 데이터를 크롤링하고 채널에 전송
        val producerJob = launch {
            toSaveDocuments.forEach { element ->
                try {
                    val rawDate = element.selectFirst(dateSelector)?.text() ?: ""
                    val title = element.selectFirst(titleSelector)?.text() ?: ""
                    val contentUrl = element.selectFirst(urlSelector)?.attr("abs:href") ?: ""
                    
                    log.info("Crawling content: $title")

                    delay(delayMillis)
                    val paragraphs = Jsoup.connect(contentUrl).get().select(contentSelector)

                    val imgList = paragraphs.select("img").map { img -> img.attr("src") }

                    val content = StringBuilder()
                    for (paragraph in paragraphs) {
                        val trimmedText = paragraph.text().replace("\\s+".toRegex(), " ").trim()
                        if (trimmedText.isNotEmpty()) {
                            content.append(trimmedText).append("\n")
                        }
                    }
                    val extractedDate = dateExtractor.extract(rawDate)

                    if (extractedDate == null) {
                        log.error("날짜 추출 실패 : $rawDate")
                        return@launch
                    }

                    urlChannel.send(InformationUrl(contentUrl = contentUrl, sourceType = sourceType))

                    informationRepository.save(
                        Information(
                            title = title,
                            content = content.toString().trim(),
                            date = extractedDate,
                            contentUrl = contentUrl,
                            imgList = imgList,
                            favicon = favicon,
                            source = sourceType.value
                        )
                    )
                } catch (e: Exception) {
                    log.error("크롤링 중 오류 발생: ${e.message}", e)
                }
            }
        }

        // 소비자: 채널에서 데이터를 읽고 저장
        val toSaveUrls = mutableListOf<InformationUrl>()
        val consumerJob = launch {
            for (url in urlChannel) {
                toSaveUrls.add(url)
            }
        }

        producerJob.join() // 생산자 작업 완료 대기
        urlChannel.close() // 생산 완료 후 채널 닫기
        consumerJob.join() // 소비자 작업 완료 대기

        val distinctUrls = toSaveUrls.distinctBy { it.contentUrl }
        informationUrlRepository.saveAll(distinctUrls)
    }
}