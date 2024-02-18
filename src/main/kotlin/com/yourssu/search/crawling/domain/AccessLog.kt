package com.yourssu.search.crawling.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field

@Document(indexName = "logstash-#{T(java.time.LocalDate).now().toString()}")
class AccessLog(
    @field:Id
    val id: String,
    val query: String? = null,
    val message: String,
    @Field(name = "_@timestamp")
    val timestamp: String? = null
) {
}