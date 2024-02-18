package com.yourssu.search.crawling.dto

data class SearchListResponse(
    val resultCount: Int,
    val resultList: List<SearchResponse>
) {
}
