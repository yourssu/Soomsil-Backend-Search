package com.yourssu.search.crawling.controller.internal

import com.yourssu.search.crawling.dto.request.SaveInformationListRequest
import com.yourssu.search.crawling.dto.request.SaveInformationRequest
import com.yourssu.search.crawling.dto.request.UpdateInformationRequest
import com.yourssu.search.crawling.service.SearchService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/internal/search")
class InternalSearchController(
    val searchService: SearchService
) {
    @PostMapping("")
    fun saveInfo(
        @RequestBody saveInformationRequest: SaveInformationRequest
    ) {
        searchService.saveInformation(
            saveInformationRequest.id,
            saveInformationRequest.title,
            saveInformationRequest.content,
            saveInformationRequest.date,
            saveInformationRequest.contentUrl,
            saveInformationRequest.imgList,
            saveInformationRequest.favicon,
            saveInformationRequest.source
        )
    }

    @DeleteMapping("/{id}")
    fun deleteInfo(
        @PathVariable("id") id: String
    ) {
        searchService.deleteInformation(id)
    }

    @PutMapping("")
    fun updateInfo(
        @RequestBody updateInformationRequest: UpdateInformationRequest
    ) {
        searchService.updateInformation(
            updateInformationRequest.id,
            updateInformationRequest.title,
            updateInformationRequest.content,
            updateInformationRequest.date,
            updateInformationRequest.contentUrl,
            updateInformationRequest.imgList,
            updateInformationRequest.favicon,
            updateInformationRequest.source
        )
    }

    @PostMapping("/articles")
    fun saveAllInfo(
        @RequestBody saveInformationListRequest: SaveInformationListRequest
    ) {
        searchService.saveInformationList(saveInformationListRequest.saveList)
    }
}
