package com.yourssu.search.global.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders

@Configuration
class SwaggerConfig(
    @Value("\${springdoc.version}")
    private val version: String,
) {
    @Bean
    fun openAPI(): OpenAPI {
        val info =
            Info()
                .title("Soomsil V2 Search API")
                .version(version)
                .description("Soomsil V2 검색 API 문서입니다.")

        return OpenAPI()
            .info(info)
    }
}