package com.yourssu.search.crawling.dto.request

import jakarta.validation.constraints.NotNull

class UpdateInformationRequest(
    @field:NotNull
    val id: String,
    val title: String,
    val content: String,
    val date: String,
    val contentUrl: String,
    val imgList: List<String>,
    val favicon: String?,
    val source: String
)
