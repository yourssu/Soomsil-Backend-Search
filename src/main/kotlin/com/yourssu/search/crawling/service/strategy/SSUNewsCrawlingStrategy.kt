package com.yourssu.search.crawling.service.strategy

import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.service.CrawlingStrategy
import com.yourssu.search.crawling.utils.CrawlingUtils
import org.jsoup.nodes.Element
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.time.measureTimedValue

@Component("ssu-news")
class SSUNewsCrawlingStrategy(
    private val crawlingUtils: CrawlingUtils
): CrawlingStrategy {
    private val log = LoggerFactory.getLogger(this::class.java)
    
    companion object {
        const val URL_SELECTOR = ".titles a"
        const val BASE_URL = "https://www.ssunews.net"
        const val UL_SELECTOR = "#section-list > ul > li"
        const val TITLE_SELECTOR = ".titles a"
        const val CONTENT_SELECTOR = "#article-view-content-div div"
        const val DATE_SELECTOR = "#section-list > ul > li > span"
    }
    
    override suspend fun crawl() {
        log.info("숭대시보 크롤링 시작")
        
        val duration = measureTimedValue {
            val allDocuments: List<List<Element>> = crawlingUtils.crawlingList(
                baseUrl = "${BASE_URL}/news/articleList.html?view_type=sm&page=",
                ulSelector = UL_SELECTOR, 
                maxConcurrentRequests = 1,
                channelCapacity = 100,
            )

            val toSaveDocuments: List<Element> =
                crawlingUtils.filteringToSaveDocuments(
                    lists = allDocuments,
                    sourceType = SourceType.SSUNEWS,
                    urlSelector = URL_SELECTOR
                )

            crawlingUtils.crawlingContents(
                toSaveDocuments = toSaveDocuments,
                titleSelector = TITLE_SELECTOR,
                contentSelector = CONTENT_SELECTOR,
                urlSelector = URL_SELECTOR,
                dateSelector = DATE_SELECTOR,
                favicon = "NONE",
                sourceType = SourceType.SSUNEWS,
                channelCapacity = 100,
                delayMillis = 10,
                syncMode = true
            )
        }
        log.info("all time use {}", duration.duration.inWholeSeconds)
    }
}