package com.yourssu.search.crawling.controller

import com.yourssu.search.crawling.service.CrawlingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
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
    }*/

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("")
    suspend fun deleteCrawling() = crawlingService.deleteData()

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/crawling/{type}")
    suspend fun crawling(@PathVariable type: String): ResponseEntity<String> {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                crawlingService.executeCrawling(type)
            } catch (e: Exception) {
                log.error("Crawling failed for $type: ${e.message}")
            }
        }
        return ResponseEntity.ok("Crawling for $type started successfully.")
    }
}
