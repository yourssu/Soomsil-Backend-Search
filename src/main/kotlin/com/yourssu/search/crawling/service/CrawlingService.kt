package com.yourssu.search.crawling.service

import com.yourssu.search.crawling.domain.Information
import com.yourssu.search.crawling.repository.InformationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class CrawlingService(
    private val informationRepository: InformationRepository,

    @Value("\${general.user-agent}")
    private val userAgent: String
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private var noticeEndNumber = 638
    private var funEndNumber = 285

    suspend fun crawlingNotice() {
        withContext(Dispatchers.IO) {
            informationRepository.deleteAll()
        }

        crawling(
            "https://scatch.ssu.ac.kr/공지사항/page",
            "ul.notice-lists li:not(.notice_head) ",
            ".notice_col3 a .d-inline-blcok.m-pt-5",
            "div.bg-white p",
            ".notice_col3 a",
            ".notice_col1 .text-info",
            noticeEndNumber,
        )
    }

    suspend fun crawlingFun() {
        withContext(Dispatchers.IO) {
            informationRepository.deleteAll()
        }

        crawling(
            "https://fun.ssu.ac.kr/ko/program/all/list/all",
            "ul.columns-4 li",
            ".content .title",
            "div .description p",
            "a",
            "small.thema_point_color.topic ~ small time",
            funEndNumber,
        )
    }

    suspend fun crawling(
        baseUrl: String,
        ulSelector: String,
        titleSelector: String,
        contentSelector: String,
        urlSelector: String,
        dateSelector: String,
        endNumber: Int,
    ) {
        val jobs = mutableListOf<Deferred<Unit>>()
        val coroutineScope = CoroutineScope(Dispatchers.IO)
        for (pageNumber in 1..endNumber) {
            val deferredJob: Deferred<Unit> =
                coroutineScope.async {
                    log.info("crawling page number : {}", pageNumber)
                    val document = Jsoup.connect("$baseUrl/$pageNumber")
                        .userAgent(userAgent)
                        .get()
                    val ul = document.select(ulSelector)

                    ul.forEach {
                        val date = it.selectFirst(dateSelector)?.text() ?: ""
                        val title = it.selectFirst(titleSelector)?.text() ?: ""
                        val contentUrl = it.selectFirst(urlSelector)?.attr("abs:href") ?: ""
                        val paragraphs = Jsoup.connect(contentUrl).get().select(contentSelector)

                        val imgList = paragraphs.select("img").map { img -> img.attr("src") }

                        val content = StringBuilder()
                        for (paragraph in paragraphs) {
                            val trimmedText = paragraph.text().replace("\\s+".toRegex(), " ").trim()
                            if (trimmedText.isNotEmpty()) {
                                content.append(trimmedText).append("\n")
                            }
                        }

                        informationRepository.save(
                            Information(
                                title = title,
                                content = content.toString().trim(),
                                date = date,
                                contentUrl = contentUrl,
                                imgList = imgList,
                            ),
                        )
                    }
                }
            jobs.add(deferredJob)
        }

        coroutineScope.async {
            jobs.awaitAll()
        }
    }

    private fun stopCrawlingNotice(ul: Elements): Boolean {
        return ul.isEmpty()
    }

    private fun stopCrawlingFun(ul: Elements): Boolean {
        return ul.select("li").attr("class") == "empty"
    }
}
