package com.yourssu.search.crawling.repository

import co.elastic.clients.elasticsearch._types.aggregations.Aggregation
import co.elastic.clients.elasticsearch._types.aggregations.AggregationBuilders
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders
import co.elastic.clients.json.JsonData
import com.yourssu.search.crawling.domain.AccessLog
import com.yourssu.search.crawling.dto.QueryCountResponse
import com.yourssu.search.crawling.dto.SearchTopQueriesResponse
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregations
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Component
class AccessLogNativeQueryRepository (
    private val elasticsearchOperations: ElasticsearchOperations
) {
    fun findTopQueries(): SearchTopQueriesResponse {
        // 전날 00:00 - 24:00까지 집계 (UTC 기준)
        val now = LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0, 0)).minusDays(1)
        val dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")
        val startTime = now.minusDays(1).format(dtf)
        val endTime = now.format(dtf)
        val basedTime = now.with(LocalTime.MIN).format(dtf)

        val rangeQuery = createRangeQuery("@timestamp", startTime, endTime)
        val matchPhraseQuery = createMatchPhraseQuery("message", "requestURI=/search, query=")
        val matchQuery = createMatchQuery("query.keyword", "")
        val aggregation = createAggregation("query.keyword", 10)

        val boolQuery = QueryBuilders.bool().must(rangeQuery, matchPhraseQuery).mustNot(matchQuery).build()._toQuery()

        val nativeQuery = NativeQuery.builder()
            .withQuery(boolQuery)
            .withAggregation("top_keywords", aggregation)
            .build()

        val searchHits = elasticsearchOperations.search(nativeQuery, AccessLog::class.java)

        val querys = if (searchHits.aggregations is ElasticsearchAggregations) {
            val buckets = (searchHits.aggregations as ElasticsearchAggregations)
                .aggregationsAsMap()["top_keywords"]!!.aggregation().aggregate.sterms().buckets().array()
            buckets.map { QueryCountResponse(it.key().stringValue(), it.docCount()) }
        } else {
            emptyList()
        }

        return SearchTopQueriesResponse(basedTime, querys)
    }

    private fun createRangeQuery(field: String, gte: String, lt: String): Query {
        return QueryBuilders.range().field(field)
            .gte(JsonData.of(gte))
            .lt(JsonData.of(lt)).build()._toQuery()
    }

    private fun createMatchPhraseQuery(field: String, query: String): Query {
        return QueryBuilders.matchPhrase().field(field)
            .query(query).build()._toQuery()
    }

    private fun createMatchQuery(field: String, query: String): Query {
        return QueryBuilders.match().field(field)
            .query(query).build()._toQuery()
    }

    private fun createAggregation(field: String, size: Int): Aggregation {
        return AggregationBuilders.terms()
            .field(field)
            .size(size)
            .build()._toAggregation()
    }
}