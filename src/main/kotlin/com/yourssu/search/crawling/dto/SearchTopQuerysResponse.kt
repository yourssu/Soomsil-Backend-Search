package com.yourssu.search.crawling.dto

data class SearchTopQuerysResponse(
    val basedTime: String,
    val querys: List<QueryCountResponse>
) {
}