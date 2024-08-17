package com.yourssu.search.crawling.domain

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "information_url")
class InformationUrl(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:Column(name = "content_url", nullable = false, unique = true, length = 500)
    val contentUrl: String,

    @field:Column(name = "source_type", nullable = false)
    @field:Enumerated(EnumType.STRING)
    val sourceType: SourceType
)
