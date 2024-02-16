package com.yourssu.search.crawling.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration

@Configuration
class ElasticSearchConfig : ElasticsearchConfiguration() {
    override fun clientConfiguration(): ClientConfiguration {
        val user = "elastic" // 초기 아이디
        val password = "yourssuElasticsearch!" // 초기 테스트 비밀번호
        val endpoint = "yourssu2024.cafe24.com:9200" // 초기 엔드 포인트

        return ClientConfiguration.builder()
            .connectedTo(endpoint)
            .withBasicAuth(user, password)
            .build()
    }
}
