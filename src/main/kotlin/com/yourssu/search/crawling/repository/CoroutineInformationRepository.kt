package com.yourssu.search.crawling.repository

import com.yourssu.search.crawling.domain.Information
import org.springframework.data.elasticsearch.repository.CoroutineElasticsearchRepository

interface CoroutineInformationRepository: CoroutineElasticsearchRepository<Information, String> {
}