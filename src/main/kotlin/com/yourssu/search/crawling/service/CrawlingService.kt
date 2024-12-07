package com.yourssu.search.crawling.service

import com.yourssu.search.crawling.repository.InformationRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CrawlingService(
    public val strategies: Map<String, CrawlingStrategy>,
    private val informationRepository: InformationRepository
) {

    suspend fun executeCrawling(strategyKey: String) {
        val strategy = strategies[strategyKey]
            ?: throw IllegalArgumentException("Invalid crawling strategy: $strategyKey")
        strategy.crawl()
    }

    @Transactional
    suspend fun deleteData() {
        withContext(Dispatchers.IO) {
            informationRepository.deleteAll()
        }
    }
}
