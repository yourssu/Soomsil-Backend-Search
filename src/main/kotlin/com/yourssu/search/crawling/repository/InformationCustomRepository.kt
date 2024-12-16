package com.yourssu.search.crawling.repository

import co.elastic.clients.elasticsearch._types.SortOptions
import co.elastic.clients.elasticsearch._types.SortOrder
import co.elastic.clients.elasticsearch._types.query_dsl.*
import co.elastic.clients.elasticsearch._types.query_dsl.FieldValueFactorModifier.Log1p
import com.yourssu.search.crawling.domain.Information
import com.yourssu.search.crawling.domain.PlatformType
import com.yourssu.search.crawling.domain.SourceType
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHits
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
    
    fun findImgNewsByDateDesc(): List<Information> {
        val pageable: Pageable = PageRequest.of(0, 2)

        val termQuery: Query = QueryBuilders.term {
            it.field("source.keyword")
                .value(SourceType.SSUNEWS.value)
        }

        val existsQuery: Query = QueryBuilders.exists {
            it.field("imgList")
        }

        val boolQuery: Query = QueryBuilders.bool {
            it.must(termQuery)
                .must(existsQuery)
        }

        val sortOptions: SortOptions = SortOptions.Builder()
            .field {
                it.field("date")
                    .order(SortOrder.Desc)
            }
            .build()

        val nativeQuery = NativeQuery.builder()
            .withQuery(boolQuery) // bool query 적용
            .withSort(sortOptions)
            .withPageable(pageable)
            .build()

        val searchHits: SearchHits<Information> = elasticsearchOperations.search(nativeQuery, Information::class.java)
        val content: List<Information> = searchHits.searchHits.map { it.content }
        return content
    }
    
    fun findTextNewsByDateDesc(platformType: PlatformType): List<Information> {
        val pageSize: Int = if (platformType == PlatformType.MOBILE) 3 else 6 
        val pageable: Pageable = PageRequest.of(0, pageSize)

        val termQuery: Query = QueryBuilders.term {
            it.field("source.keyword")
                .value(SourceType.SSUNEWS.value)
        }

        val boolQuery: Query = QueryBuilders.bool {
            it.must(termQuery)
                .mustNot(QueryBuilders.exists { it.field("imgList") })
        }
        
        val sortOptions: SortOptions = SortOptions.Builder()
            .field {
                it.field("date")
                    .order(SortOrder.Desc)
            }
            .build()
        
        val nativeQuery = NativeQuery.builder()
            .withQuery(boolQuery) // bool query 적용
            .withSort(sortOptions)
            .withPageable(pageable)
            .build()

        val searchHits: SearchHits<Information> = elasticsearchOperations.search(nativeQuery, Information::class.java)
        val content: List<Information> = searchHits.searchHits.map { it.content }
        return content
    }
}
