package com.yourssu.search.crawling.service

import com.yourssu.search.crawling.dto.SearchListResponse
import com.yourssu.search.crawling.dto.SearchResponse
import com.yourssu.search.crawling.dto.SearchTopQueriesResponse
import com.yourssu.search.crawling.repository.AccessLogNativeQueryRepository
import com.yourssu.search.crawling.repository.InformationRepository
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val informationRepository: InformationRepository,
    private val accessLogNativeQueryRepository: AccessLogNativeQueryRepository,
) {
    fun search(
        query: String,
        pageable: Pageable,
    ): SearchListResponse {
        val result = informationRepository.findByInfoOrderByScoreDesc(query, pageable)
        val informations = result.map { SearchResponse.of(it) }.toList()
        return SearchListResponse(
            totalCount = result.totalElements,
            resultCount = informations.size,
            resultList = informations,
        )
    }

    fun searchTopQueries(): SearchTopQueriesResponse {
        return accessLogNativeQueryRepository.findTopQueries()
    }
}
