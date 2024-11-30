package com.yourssu.search.crawling.scheduler

import com.yourssu.search.crawling.service.CrawlingService
import kotlinx.coroutines.runBlocking
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class CrawlingScheduler(
    private val crawlingService: CrawlingService
) {
    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정 12시 실행
    fun scheduleCrawling() = runBlocking {
        crawlingService.strategies.keys.forEach { key -> // strategies의 의존성 주입 된 객체 순회
            crawlingService.executeCrawling(key)
        }
    }
}
