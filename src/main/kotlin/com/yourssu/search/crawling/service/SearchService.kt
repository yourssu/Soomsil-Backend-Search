package com.yourssu.search.crawling.service

import com.yourssu.search.crawling.domain.Information
import com.yourssu.search.crawling.dto.SearchListResponse
import com.yourssu.search.crawling.dto.SearchResponse
import com.yourssu.search.crawling.dto.SearchTopQuerysResponse
import com.yourssu.search.crawling.dto.request.SaveInformationRequest
import com.yourssu.search.crawling.repository.AccessLogNativeQueryRepository
import com.yourssu.search.crawling.repository.InformationRepository
import com.yourssu.search.global.exception.ElasticConnectionException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class SearchService(
    private val informationRepository: InformationRepository,
    private val accessLogNativeQueryRepository: AccessLogNativeQueryRepository
) {
    fun search(
        query: String,
        pageable: Pageable
    ): SearchListResponse {
        val result = informationRepository.findByInfoOrderByScoreDesc(query, pageable)
        val informations = result.map { SearchResponse.of(it) }.toList()
        return SearchListResponse(
            totalCount = result.totalElements,
            resultCount = informations.size,
            resultList = informations
        )
    }

    fun searchTopQuerys(): SearchTopQuerysResponse {
        return accessLogNativeQueryRepository.findTopQuerys()
    }

    fun saveInformation(
        id: String,
        title: String,
        content: String,
        date: String,
        contentUrl: String,
        imgList: List<String>,
        favicon: String?,
        source: String
    ) {
        val information = Information(
            id = id,
            title = title,
            content = content,
            date = date,
            contentUrl = contentUrl,
            imgList = imgList,
            favicon = favicon,
            source = source
        )
        informationRepository.save(information)
        verifySaveArticle(id, title)
    }

    private fun verifySaveArticle(
        id: String,
        title: String
    ) {
        val savedArticle = informationRepository.findById(id)
        if (savedArticle.isEmpty || (savedArticle.get().title != title)) {
            throw ElasticConnectionException()
        }
    }

    fun deleteInformation(id: String) {
        informationRepository.deleteById(id)
        verifyDeleteArticle(id)
    }

    private fun verifyDeleteArticle(id: String) {
        if (informationRepository.findById(id).isPresent) {
            throw ElasticConnectionException()
        }
    }

    fun updateInformation(
        id: String,
        title: String,
        content: String,
        date: String,
        contentUrl: String,
        imgList: List<String>,
        favicon: String?,
        source: String
    ) {
        deleteInformation(id)
        saveInformation(id, title, content, date, contentUrl, imgList, favicon, source)
    }

    fun saveInformationList(informationRequests: List<SaveInformationRequest>) {
        val informationList = informationRequests.map {
            Information(
                id = it.id,
                title = it.title,
                content = it.content,
                date = it.date,
                contentUrl = it.contentUrl,
                imgList = it.imgList,
                favicon = it.favicon,
                source = it.source
            )
        }
        informationRepository.saveAll(informationList)
    }
}
