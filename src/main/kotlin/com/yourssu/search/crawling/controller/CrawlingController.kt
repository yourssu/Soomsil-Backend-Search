package com.yourssu.search.crawling.controller

import com.yourssu.search.crawling.service.CrawlingService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class CrawlingController(
    private val crawlingService: CrawlingService
) {
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crawling/fun")
    suspend fun crawlingFun(): ResponseEntity<String> {
        return try {
            crawlingService.crawlingFun()
            ResponseEntity.ok("Crawling started successfully.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Crawling failed: ${e.message}")
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crawling/notice")
    suspend fun crawlingNotice(): ResponseEntity<String> {
        return try {
            crawlingService.crawlingNotice()
            ResponseEntity.ok("Crawling started successfully.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Crawling failed: ${e.message}")
        }
    }
}
