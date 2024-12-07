package com.yourssu.search.crawling.repository

import com.yourssu.search.crawling.domain.InformationUrl
import com.yourssu.search.crawling.domain.SourceType
import kotlinx.coroutines.flow.Flow
import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface InformationUrlRepository : CoroutineCrudRepository<InformationUrl, Long> {
    fun findAllBySourceType(sourceType: SourceType): Flow<InformationUrl>
}
