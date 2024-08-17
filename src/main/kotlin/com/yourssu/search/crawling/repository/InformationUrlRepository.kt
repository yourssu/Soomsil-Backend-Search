package com.yourssu.search.crawling.repository

import com.yourssu.search.crawling.domain.InformationUrl
import com.yourssu.search.crawling.domain.SourceType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface InformationUrlRepository : JpaRepository<InformationUrl, String> {
    fun findAllBySourceType(sourceType: SourceType): List<InformationUrl>
}
