package com.yourssu.search.crawling.repository

import com.yourssu.search.crawling.domain.Information
import org.springframework.data.elasticsearch.annotations.Query
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface InformationRepository : ElasticsearchRepository<Information, String> {
    @Query("{\"multi_match\":{\"query\":\"?0\",\"fields\":[\"title^7\",\"content^3\"],\"fuzziness\":\"AUTO\"}}")
    fun findByInfoOrderByScoreDesc(query: String): List<Information>
}
