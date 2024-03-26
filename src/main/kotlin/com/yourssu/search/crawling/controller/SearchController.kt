package com.yourssu.search.crawling.controller

import com.yourssu.search.crawling.dto.SearchListResponse
import com.yourssu.search.crawling.dto.SearchTopQueriesResponse
import com.yourssu.search.crawling.service.SearchService
import net.logstash.logback.marker.Markers.append
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/search")
class SearchController(
    private val searchService: SearchService
) {
    private val logger: Logger = LoggerFactory.getLogger(SearchController::class.java)

    @GetMapping
    fun search(
        @RequestParam query: String,
        @RequestParam page: Int
    ): SearchListResponse {
        logger.info(append("query", query), "requestURI=/search, query={}", query)
        val pageable = PageRequest.of(page, 10)
        return searchService.search(query, pageable)
    }

    @GetMapping("/topQueries")
    fun getTopKeywords(): SearchTopQueriesResponse {
        return searchService.searchTopQueries()
    }
}
