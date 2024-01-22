package com.yourssu.search.crawling.service

import com.yourssu.search.crawling.repository.InformationRepository
import com.yourssu.search.crawling.dto.SearchListResponse
import com.yourssu.search.crawling.dto.SearchResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val informationRepository: InformationRepository
) {
    fun search(query: String, pageable: Pageable): SearchListResponse {
        val informations = informationRepository.findByInfoOrderByScoreDesc(query, pageable).map { SearchResponse.of(it) }.toList()

        return SearchListResponse(
            resultList = informations
        )
    }
}