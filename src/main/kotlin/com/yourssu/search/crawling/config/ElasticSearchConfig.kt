package com.yourssu.search.crawling.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration

@Configuration
class ElasticSearchConfig : ElasticsearchConfiguration() {
    override fun clientConfiguration(): ClientConfiguration {
        val user = "elastic"
        val password = "yourssuElasticsearch!" // 초기 테스트 비밀번호

        return ClientConfiguration.builder()
            .connectedTo("yourssu2024.cafe24.com:9200")
            .withBasicAuth(user, password)
            .build()
    }
}
