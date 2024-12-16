package com.yourssu.search.crawling.utils

import org.jsoup.Jsoup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.time.LocalDate

class DateExtractorTest {
    @DisplayName("숭대시보 날짜 파싱 테스트")
    @Test
    fun dateParsingTest() {
        val dateExtractor = DateExtractor()
        val text: String = Jsoup
            .connect("https://www.ssunews.net/news/articleList.html?view_type=sm")
            .get()
            .selectFirst("#section-list > ul > li:nth-child(1)")
            ?.select("#section-list > ul > li:nth-child(1) > span")
            ?.text() ?: ""

        assertEquals("2024.12.06", text)
        println("text = $text")

        val extractedText: LocalDate? = dateExtractor.extract(text)
        assertEquals(LocalDate.of(2024, 12, 6), extractedText)
        println("extractedText = $extractedText")
    }
    
    @DisplayName("숭대시보 이미지 추출 테스트")
    @Test
    fun imgExtractTest() {
        val imgList: List<String> = Jsoup
            .connect("https://www.ssunews.net/news/articleView.html?idxno=5346")
            .get()
            .select("#article-view-content-div div")
            .select("img")
            .map { it.attr("src") }

        println("imgList = ${imgList}")
    }
}