package com.yourssu.search.crawling.controller

import com.yourssu.search.crawling.dto.SearchListResponse
import com.yourssu.search.crawling.service.SearchService
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
    @GetMapping
    fun search(@RequestParam query: String,
               @RequestParam page: Int): SearchListResponse {
        val pageable = PageRequest.of(page, 10)
        return searchService.search(query, pageable)
    }
}