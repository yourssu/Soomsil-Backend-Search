package com.yourssu.search.crawling.dto

data class SearchTopQueriesResponse(
    val basedTime: String,
    val queries: List<QueryCountResponse>
) {
}