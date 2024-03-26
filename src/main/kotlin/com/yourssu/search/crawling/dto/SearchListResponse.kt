package com.yourssu.search.crawling.dto

data class SearchListResponse(
    val totalCount: Long,
    val resultCount: Int,
    val resultList: List<SearchResponse>
)
