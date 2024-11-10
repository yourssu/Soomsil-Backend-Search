package com.yourssu.search.crawling.domain

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import org.springframework.data.relational.core.mapping.Column

@Table("information_url")  // 테이블 이름을 지정
class InformationUrl(
    @Id  // 기본 키로 지정
    val id: Long? = null,

    @Column("content_url")  // 열 이름을 매핑
    val contentUrl: String,

    @Column("source_type")  // 열 이름을 매핑
    val sourceType: SourceType
)

