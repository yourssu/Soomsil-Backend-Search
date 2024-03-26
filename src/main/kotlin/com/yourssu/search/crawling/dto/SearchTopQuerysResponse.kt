package com.yourssu.search.crawling.dto

data class SearchTopQueriesResponse(
    val basedTime: String,
    val querys: List<QueryCountResponse>
)
