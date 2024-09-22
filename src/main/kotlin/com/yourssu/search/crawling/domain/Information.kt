package com.yourssu.search.crawling.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document
import org.springframework.data.elasticsearch.annotations.Field
import org.springframework.data.elasticsearch.annotations.FieldType
import java.time.LocalDate

@Document(indexName = "information")
class Information(
    @field:Id
    val id: String? = null,
    var title: String,
    var content: String,
    @Field(type = FieldType.Date, format = [], pattern = ["yyyy.MM.dd"])
    val date: LocalDate,
    var contentUrl: String,
    var imgList: List<String>,
    var favicon: String?,
    var source: String
)
