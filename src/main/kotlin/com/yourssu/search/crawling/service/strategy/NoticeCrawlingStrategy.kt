package com.yourssu.search.crawling.service.strategy

import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.service.CrawlingStrategy
import com.yourssu.search.crawling.utils.CrawlingUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import kotlin.time.measureTimedValue

@Component("notice")
class NoticeCrawlingStrategy(
    private val crawlingUtils: CrawlingUtils
): CrawlingStrategy {
    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun crawl() {
        val urlSelector = ".notice_col3 a"
        val duration = measureTimedValue {
            val elements = crawlingUtils.crawlingList(
                "https://scatch.ssu.ac.kr/공지사항/page",
                "ul.notice-lists li:not(.notice_head) ",
                638
            )

            crawlingUtils.crawlingContents(
                crawlingUtils.filteringAlreadySavedData(elements, SourceType.NOTICE, urlSelector),
                ".notice_col3 a .d-inline-blcok.m-pt-5",
                "div.bg-white p",
                urlSelector,
                ".notice_col1 .text-info",
                "https://scatch.ssu.ac.kr/wp-content/uploads/sites/5/2019/07/cropped-favicon-32x32.png", //getFavicon("https://scatch.ssu.ac.kr/공지사항"),
                "공지사항",
                SourceType.NOTICE
            )
        }
        log.info("all time use {}", duration.duration.inWholeSeconds)
    }
}