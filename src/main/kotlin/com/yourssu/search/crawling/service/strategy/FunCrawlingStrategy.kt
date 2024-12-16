package com.yourssu.search.crawling.service.strategy

import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.service.CrawlingStrategy
import com.yourssu.search.crawling.utils.CrawlingUtils
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
        log.info("펀시스템 크롤링 시작")
        val urlSelector = "a"
        val duration = measureTimedValue {
            val allDocuments: List<List<Element>> = crawlingUtils.crawlingList(
                baseUrl = "https://fun.ssu.ac.kr/ko/program/all/list/all/",
                ulSelector = "ul.columns-4 li",
                channelCapacity = 10000
            )

            val toSaveDocuments: List<Element> =
                crawlingUtils.filteringToSaveDocuments(
                    lists = allDocuments, 
                    sourceType = SourceType.FUN, 
                    urlSelector = urlSelector
                )

            crawlingUtils.crawlingContents(
                toSaveDocuments = toSaveDocuments,
                titleSelector = ".content .title",
                contentSelector = "div .description p",
                urlSelector = urlSelector,
                dateSelector = "small.thema_point_color.topic ~ small time",
                favicon = "https://fun.ssu.ac.kr/attachment/view/51/favicon.ico",
                sourceType = SourceType.FUN
            )
        }
        log.info("all time use {}", duration.duration.inWholeSeconds)
    }
}