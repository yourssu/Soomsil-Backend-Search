package com.yourssu.search.crawling.dto

import java.time.LocalDate

data class NewsResponse(
    val imgNews: List<ImgNewsResponse>,
    val textNews: List<TextNewsResponse>
)

data class ImgNewsResponse(
    val thumbnail: String,
    val title: String,
    val date: LocalDate,
    val pageUrl: String 
) {
    companion object {
        fun of(thumbnail: String, title: String, date: LocalDate, pageUrl: String): ImgNewsResponse {
            return ImgNewsResponse(thumbnail, title, date, pageUrl)
        }
    }
}

data class TextNewsResponse(
    val title: String,
    val date: LocalDate,
    val pageUrl: String,
) {
    companion object {
        fun of(title: String, date: LocalDate, pageUrl: String): TextNewsResponse {
            return TextNewsResponse(title, date, pageUrl)
        }
    }
}