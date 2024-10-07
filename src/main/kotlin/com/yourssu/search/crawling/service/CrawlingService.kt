package com.yourssu.search.crawling.service

import com.yourssu.search.crawling.domain.Information
import com.yourssu.search.crawling.domain.InformationUrl
import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.repository.InformationRepository
import com.yourssu.search.crawling.repository.InformationUrlRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.util.regex.Pattern
import kotlin.time.measureTimedValue

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
