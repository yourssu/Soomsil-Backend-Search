package com.yourssu.search.crawling.service

interface CrawlingStrategy {
    suspend fun crawl()
}