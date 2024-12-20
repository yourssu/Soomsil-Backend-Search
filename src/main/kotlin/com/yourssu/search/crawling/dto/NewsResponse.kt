package com.yourssu.search.crawling.dto

import java.time.LocalDate

data class NewsResponse(
    val imgNews: List<ImgNewsResponse>,
    val textNews: List<TextNewsResponse>
)

data class ImgNewsResponse(
    val thumbnail: String,
    val title: String,
    val date: String,
    val pageUrl: String 
) {
    companion object {
        fun of(thumbnail: String, title: String, date: LocalDate, pageUrl: String): ImgNewsResponse {
            val dateMonthValue = if (date.monthValue < 10) "0${date.monthValue}" else date.monthValue.toString()
            val dateDayOfMonth = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else date.dayOfMonth.toString()
            
            val dateString = StringBuilder()
                .append(date.year)
                .append(".")
                .append(dateMonthValue)
                .append(".")
                .append(dateDayOfMonth)
                .toString()
            
            return ImgNewsResponse(
                thumbnail = thumbnail, 
                title = title, 
                date = dateString,
                pageUrl = pageUrl
            )
        }
    }
}

data class TextNewsResponse(
    val title: String,
    val date: String,
    val pageUrl: String,
) {
    companion object {
        fun of(title: String, date: LocalDate, pageUrl: String): TextNewsResponse {
            val dateMonthValue = if (date.monthValue < 10) "0${date.monthValue}" else date.monthValue.toString()
            val dateDayOfMonth = if (date.dayOfMonth < 10) "0${date.dayOfMonth}" else date.dayOfMonth.toString()
            
            val dateString = StringBuilder()
                .append(date.year)
                .append(".")
                .append(dateMonthValue)
                .append(".")
                .append(dateDayOfMonth)
                .toString()
            
            return TextNewsResponse(
                title = title, 
                date = dateString,
                pageUrl = pageUrl
            )
        }
    }
}