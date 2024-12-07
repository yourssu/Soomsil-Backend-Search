package com.yourssu.search.repository

import com.yourssu.search.crawling.domain.InformationUrl
import com.yourssu.search.crawling.domain.SourceType
import com.yourssu.search.crawling.repository.InformationUrlRepository
import com.yourssu.search.crawling.utils.CrawlingUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.assertj.core.api.Assertions.assertThat
import org.hibernate.validator.internal.util.Contracts.assertNotNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class InformationUrlRepositoryTest {

    @Autowired
    private lateinit var repository: InformationUrlRepository

    @Autowired
    private lateinit var service: CrawlingUtils

    @Test
    fun `save information`() = runTest {
        // Arrange: 테스트용 데이터 준비
        val info = InformationUrl(
            contentUrl = "http://example.com",
            sourceType = SourceType.NOTICE
        )

        val savedInfo = repository.save(info)

        assertNotNull(savedInfo.id, "ID should not be null after saving")
        assert(savedInfo.contentUrl == info.contentUrl)
        assert(savedInfo.sourceType == info.sourceType)
    }

    @Test
    fun `find by sourceType`() = runBlocking {
        val noticeUrls = repository.findAllBySourceType(SourceType.FUN).toList()

        println("Found ${noticeUrls.size} URLs for SourceType.NOTICE")
    }

    /*@Test
    fun `test transactional rollback`() = runTest {
        // Arrange: 테스트 데이터 준비
        val urls = (1..100).map {
            InformationUrl(
                contentUrl = "http://example.com/$it",
                sourceType = if (it % 10 == 0) SourceType.FUN else SourceType.NOTICE
            )
        }

        try {
            service.saveAllWithRollback(urls)
        } catch (ex: RuntimeException) {
            println("Exception occurred: ${ex.message}")
        }

        // Assert: 데이터베이스에 남아 있는 URL 확인
        val remainingUrls = repository.findAll().toList()
        println("Remaining URLs: ${remainingUrls.size}")
        assertEquals(0, remainingUrls.size, "All changes should have been rolled back")
    }*/
}
