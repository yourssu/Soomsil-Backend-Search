package com.yourssu.search.crawling.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate

@Configuration
class R2dbcConfig(private val connectionFactory: ConnectionFactory) {

    @Bean
    fun r2dbcEntityTemplate(): R2dbcEntityTemplate {
        return R2dbcEntityTemplate(connectionFactory)
    }
}
