package com.yourssu.search.crawling.service.strategy

import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.service.CrawlingStrategy
import com.yourssu.search.crawling.utils.CrawlingUtils
import kotlinx.coroutines.Deferred
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.time.measureTimedValue

@Component("notice")
class NoticeCrawlingStrategy(
    private val crawlingUtils: CrawlingUtils
): CrawlingStrategy {
    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun crawl() {
        val urlSelector = ".notice_col3 a"
        val duration = measureTimedValue {
            val allDocuments: List<Deferred<List<Element>>> = crawlingUtils.crawlingList(
                "https://scatch.ssu.ac.kr/공지사항/page",
                "ul.notice-lists li:not(.notice_head) ",
                638
            )

            val toSaveDocuments: List<Element> =
                crawlingUtils.filteringToSaveDocuments(allDocuments, SourceType.NOTICE, urlSelector)

            crawlingUtils.crawlingContents(
                toSaveDocuments = toSaveDocuments,
                titleSelector = ".notice_col3 a .d-inline-blcok.m-pt-5",
                contentSelector = "div.bg-white p",
                urlSelector = urlSelector,
                dateSelector = ".notice_col1 .text-info",
                favicon = "https://scatch.ssu.ac.kr/wp-content/uploads/sites/5/2019/07/cropped-favicon-32x32.png",
                source = "공지사항",
                sourceType = SourceType.NOTICE
            )
        }
        log.info("all time use {}", duration.duration.inWholeSeconds)
    }
}