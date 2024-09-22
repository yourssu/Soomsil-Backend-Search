package com.yourssu.search.crawling.dto

import com.yourssu.search.crawling.domain.Information
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class SearchResponse(
    val id: String?,
    val title: String,
    val link: String,
    val content: String,
    val date: String,
    val thumbnailCount: Int,
    val thumbnail: List<String>,
    val favicon: String?,
    val source: String
) {
    companion object {
        fun of(information: Information): SearchResponse {
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd")

            return SearchResponse(
                id = information.id,
                title = information.title,
                link = information.contentUrl,
                content = information.content,
                date = information.date.format(dateFormatter),
                thumbnailCount = information.imgList.size,
                thumbnail = information.imgList,
                favicon = information.favicon,
                source = information.source
            )
        }
    }
}
