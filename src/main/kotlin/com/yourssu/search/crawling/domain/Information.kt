package com.yourssu.search.crawling.domain

import org.springframework.data.annotation.Id
import org.springframework.data.elasticsearch.annotations.Document

@Document(indexName = "information")
class Information(
    @field:Id
    val id: String? = null,
    var title: String,
    var content: String,
    val date: String,
    var contentUrl: String,
    var imgList: List<String>,
)
