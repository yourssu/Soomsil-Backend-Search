package com.yourssu.search.crawling.service.strategy

import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.service.CrawlingStrategy
import com.yourssu.search.crawling.utils.CrawlingUtils
import kotlinx.coroutines.Deferred
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.time.measureTimedValue

@Component("fun")
class FunCrawlingStrategy(
    private val crawlingUtils: CrawlingUtils
): CrawlingStrategy {
    private val log = LoggerFactory.getLogger(this::class.java)

    override suspend fun crawl() {
        val urlSelector = "a"
        val duration = measureTimedValue {
            val allDocuments: List<Deferred<List<Element>>> = crawlingUtils.crawlingList(
                "https://fun.ssu.ac.kr/ko/program/all/list/all",
                "ul.columns-4 li",
                285
            )

            val toSaveDocuments: List<Element> =
                crawlingUtils.filteringToSaveDocuments(allDocuments, SourceType.FUN, urlSelector)

            crawlingUtils.crawlingContents(
                toSaveDocuments = toSaveDocuments,
                titleSelector = ".content .title",
                contentSelector = "div .description p",
                urlSelector = urlSelector,
                dateSelector = "small.thema_point_color.topic ~ small time",
                favicon = "https://fun.ssu.ac.kr/attachment/view/51/favicon.ico",
                source = "펀시스템",
                sourceType = SourceType.FUN
            )
        }
        log.info("all time use {}", duration.duration.inWholeSeconds)
    }
}