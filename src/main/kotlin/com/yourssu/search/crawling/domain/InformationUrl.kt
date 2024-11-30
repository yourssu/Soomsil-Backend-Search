package com.yourssu.search.crawling.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table("information_url")
class InformationUrl(
    @Id
    val id: Long? = null,

    @Column("content_url")
    val contentUrl: String,

    @Column("source_type")
    val sourceType: SourceType
)
