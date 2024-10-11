package com.yourssu.search.crawling.service

import com.yourssu.search.crawling.repository.InformationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service

@Service
class CrawlingService(
    private val strategies: Map<String, CrawlingStrategy>,
    private val informationRepository: InformationRepository,
) {

    suspend fun executeCrawling(strategyKey: String) {
        val strategy = strategies[strategyKey]
            ?: throw IllegalArgumentException("Invalid crawling strategy: $strategyKey")
        strategy.crawl()
    }

    suspend fun deleteData() {
        withContext(Dispatchers.IO) {
            informationRepository.deleteAll()
        }
    }
}
