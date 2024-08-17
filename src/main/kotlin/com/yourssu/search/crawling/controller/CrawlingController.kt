package com.yourssu.search.crawling.controller

import com.yourssu.search.crawling.service.CrawlingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
class CrawlingController(
    private val crawlingService: CrawlingService
) {
    private val log = LoggerFactory.getLogger(this::class.java)

    /*@ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crawling/fun")
    suspend fun crawlingFun(): ResponseEntity<String> {
        return try {
            crawlingService.crawlingFun()
            ResponseEntity.ok("Crawling started successfully.")
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Crawling failed: ${e.message}")
        }
    }*/

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crawling/fun")
    suspend fun crawlingFun(): ResponseEntity<String> {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                crawlingService.crawlingFun()
            } catch (e: Exception) {
                log.error("Crawling failed: ${e.message}")
            }
        }
        return ResponseEntity.ok("Crawling started successfully.")
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crawling/notice")
    suspend fun crawlingNotice(): ResponseEntity<String> {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                crawlingService.crawlingNotice()
            } catch (e: Exception) {
                log.error("Crawling failed: ${e.message}")
            }
        }
        return ResponseEntity.ok("Crawling started successfully.")
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    suspend fun deleteCrawling() = crawlingService.deleteData()
}
