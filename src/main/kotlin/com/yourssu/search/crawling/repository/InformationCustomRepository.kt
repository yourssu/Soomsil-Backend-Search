package com.yourssu.search.crawling.repository

import co.elastic.clients.elasticsearch._types.query_dsl.*
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier.Log1p
import com.yourssu.search.crawling.domain.Information
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Component

@Component
class InformationCustomRepository(
    private val elasticsearchOperations: ElasticsearchOperations
) {
    fun search(query: String, pageable: Pageable): Page<Information> {
        val multiMatch = MultiMatchQuery.Builder()
            .query(query)
            .fields(listOf("title", "content"))
            .build()

        val fieldValueFactor = FieldValueFactorScoreFunction.Builder()
            .field("date")
            .factor(0.5)
            .missing(0.0)
            .modifier(Log1p)
            .build()

        val functionScore = FunctionScore.Builder()
            .fieldValueFactor(fieldValueFactor)
            .build()

        val functionScoreQuery = FunctionScoreQuery.Builder()
            .query(multiMatch._toQuery())
            .functions(listOf(functionScore))
            .boostMode(FunctionBoostMode.Sum)
            .scoreMode(FunctionScoreMode.Sum)
            .build()

        val nativeQuery = NativeQuery.builder()
            .withQuery(functionScoreQuery._toQuery())
            .withPageable(pageable)
            .build()

        val searchHits = elasticsearchOperations.search(nativeQuery, Information::class.java)
        val content = searchHits.searchHits.map { it.content }
        return PageImpl(content, pageable, searchHits.totalHits)
    }
}
