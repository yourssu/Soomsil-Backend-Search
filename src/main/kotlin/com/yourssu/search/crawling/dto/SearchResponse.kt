package com.yourssu.search.crawling.dto

import com.yourssu.search.crawling.domain.Information

data class SearchResponse(
    val title: String,
    val link: String,
    val content: String,
    val date: String,
    val thumbnail: List<String>
) {
    companion object {
        fun of(information: Information): SearchResponse {
            return SearchResponse(
                title = information.title,
                link = information.contentUrl,
                content = information.content,
                date = information.date,
                thumbnail = information.imgList
            )
        }
    }
}
