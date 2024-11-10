package com.yourssu.search

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@EnableR2dbcRepositories
@EnableR2dbcAuditing
@SpringBootApplication
class SearchApplication

fun main(args: Array<String>) {
    runApplication<SearchApplication>(*args)
}
