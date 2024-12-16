package com.yourssu.search.crawling.utils

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.util.regex.Pattern

@Component
class DateExtractor {
    private val log = LoggerFactory.getLogger(this::class.java)
    
    fun extract(dateStr: String): LocalDate? {
        val datePattern = Pattern.compile("(\\d{4})\\.(\\d{2})\\.(\\d{2})")
        val matcher = datePattern.matcher(dateStr)

        return if (matcher.find()) {
            val year = matcher.group(1).toInt()
            val month = matcher.group(2).toInt()
            val day = matcher.group(3).toInt()

            try {
                LocalDate.of(year, month, day)
            } catch (e: Exception) {
                log.error("날짜 변환 실패 : $dateStr")
                null // 날짜 변환 실패 시 null 반환
            }
        } else {
            log.error("정규표현식에 맞지 않음 : $dateStr")
            null // 정규표현식에 맞지 않으면 null 반환
        }
    }
}