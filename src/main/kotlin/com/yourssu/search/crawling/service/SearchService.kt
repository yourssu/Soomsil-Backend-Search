package com.yourssu.search.crawling.service

import com.yourssu.search.crawling.domain.Information
import com.yourssu.search.crawling.domain.PlatformType
import com.yourssu.search.crawling.dto.ImgNewsResponse
import com.yourssu.search.crawling.dto.NewsResponse
import com.yourssu.search.crawling.dto.SearchListResponse
import com.yourssu.search.crawling.dto.SearchResponse
import com.yourssu.search.crawling.dto.SearchTopQueriesResponse
import com.yourssu.search.crawling.dto.TextNewsResponse
import com.yourssu.search.crawling.dto.request.SaveInformationRequest
import com.yourssu.search.crawling.repository.AccessLogNativeQueryRepository
import com.yourssu.search.crawling.repository.InformationCustomRepository
import com.yourssu.search.crawling.repository.InformationRepository
import com.yourssu.search.global.exception.ElasticConnectionException
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class SearchService(
    private val informationRepository: InformationRepository,
    private val accessLogNativeQueryRepository: AccessLogNativeQueryRepository,
    private val customInformationRepository: InformationCustomRepository
) {
    fun search(
        query: String,
        pageable: Pageable
    ): SearchListResponse {
        val result = customInformationRepository.search(query, pageable)
        val informations = result.map { SearchResponse.of(it) }.toList()
        return SearchListResponse(
            totalCount = result.totalElements,
            resultCount = informations.size,
            resultList = informations
        )
    }

    fun searchTopQueries(): SearchTopQueriesResponse {
        return accessLogNativeQueryRepository.findTopQueries()
    }

    fun saveInformation(
        id: String,
        title: String,
        content: String,
        date: LocalDate,
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
        date: LocalDate,
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
    
    fun getNews(platformType: PlatformType): NewsResponse {
        val findImgNewsByDateDesc: List<Information> = customInformationRepository.findImgNewsByDateDesc()
        val findTextNewsByDateDesc: List<Information> = customInformationRepository.findTextNewsByDateDesc(platformType)

        val imgNewsResponse: List<ImgNewsResponse> = findImgNewsByDateDesc.map { 
            ImgNewsResponse.of(
                it.imgList[0], 
                it.title, 
                it.date,
                it.contentUrl
            ) 
        }
        
        val textNewsResponse: List<TextNewsResponse> = findTextNewsByDateDesc.map { 
            TextNewsResponse.of(
                it.title, 
                it.date,
                it.contentUrl
            ) 
        }
        
        return NewsResponse(imgNewsResponse, textNewsResponse)
    }
}
