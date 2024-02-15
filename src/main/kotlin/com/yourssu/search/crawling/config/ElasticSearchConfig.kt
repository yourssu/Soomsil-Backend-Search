package com.yourssu.search.crawling.config

import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.springframework.context.annotation.Configuration
import org.springframework.data.elasticsearch.client.ClientConfiguration
import org.springframework.data.elasticsearch.client.elc.ElasticsearchClients
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration
import java.io.BufferedInputStream
import java.io.FileInputStream
import java.security.KeyStore
import java.security.cert.CertificateFactory
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManagerFactory

@Configuration
class ElasticSearchConfig : ElasticsearchConfiguration() {
    override fun clientConfiguration(): ClientConfiguration {
        val user = "elastic"
        val password = "yourssuElasticsearch!"

        return ClientConfiguration.builder()
            .connectedTo("yourssu2024.cafe24.com:9200")
            .withBasicAuth(user, password)
            .build()
    }
}
