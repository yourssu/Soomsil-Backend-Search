package com.yourssu.search.crawling.service.strategy

import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.service.CrawlingStrategy
import com.yourssu.search.crawling.utils.CrawlingUtils
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
            val elements = crawlingUtils.crawlingList(
                "https://fun.ssu.ac.kr/ko/program/all/list/all",
                "ul.columns-4 li",
                285
            )

            crawlingUtils.crawlingContents(
                crawlingUtils.filteringAlreadySavedData(elements, SourceType.FUN, urlSelector),
                ".content .title",
                "div .description p",
                urlSelector,
                "small.thema_point_color.topic ~ small time",
                "https://fun.ssu.ac.kr/attachment/view/51/favicon.ico",
                "펀시스템",
                SourceType.FUN
            )
        }
        log.info("all time use {}", duration.duration.inWholeSeconds)
    }
}