package com.yourssu.search.laboratory

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import kotlin.time.measureTimedValue

class ConcurrencyTest {
    private val testScope = CoroutineScope(Dispatchers.IO)
    private val mutex = Mutex()

    private fun setupDataFixture(size: Int): List<String> {
        return List(size) {
            "$it"
        }
    }

    private suspend fun simulateCrawlingWork() {
        delay(22)
    }

    private suspend fun mutexFunction(
        data: List<String>
    ): Pair<List<String>, Long> {
        val newStrings = mutableListOf<String>()

        val duration = measureTimedValue {
            val contentJobs: List<Job> = data.map { value ->
                testScope.launch {
                    simulateCrawlingWork()
                    mutex.withLock {
                        newStrings.add(value)
                    }
                }
            }
            contentJobs.joinAll()
        }

        return Pair(newStrings, duration.duration.inWholeMilliseconds)
    }

    private suspend fun channelFunction(
        data: List<String>
    ): Pair<List<String>, Long> {
        val newUrls = mutableListOf<String>()
        val duration = measureTimedValue {
            val channel = Channel<String>(Channel.UNLIMITED)

            val senderJobs = data.map { value ->
                testScope.launch {
                    simulateCrawlingWork()
                    channel.send(value)
                }
            }

            senderJobs.joinAll()
            channel.close()
            
            for (i in channel) {
                newUrls.add(i)
            }
        }

        return Pair(newUrls, duration.duration.inWholeMilliseconds)
    }

    @Test
    @DisplayName("Mutex vs Channel 실제 병렬 성능 비교")
    fun comparePerformanceTest() {
        val testRounds = 100
        val dataAmount = 10

        val mutexTimes = mutableListOf<Long>()
        val channelTimes = mutableListOf<Long>()

        runBlocking {
            // 실제 테스트
            for (i in 1..testRounds) {
                val data = setupDataFixture(dataAmount)

                val (mutexResult, mutexTime) = mutexFunction(data)

                val (channelResult, channelTime) = channelFunction(data)

                assertEquals(dataAmount, mutexResult.size)
                mutexTimes.add(mutexTime)
                assertEquals(dataAmount, channelResult.size)
                channelTimes.add(channelTime)

                println("Round $i - Mutex: ${mutexTime}ms, Channel: ${channelTime}ms")
            }
        }
        
        val mutexAvg = mutexTimes.average()
        val mutexMin = mutexTimes.min()
        val mutexMax = mutexTimes.max()
        
        val channelAvg = channelTimes.average()
        val channelMin = channelTimes.min()
        val channelMax = channelTimes.max()

        val improvement = ((mutexAvg - channelAvg) / mutexAvg) * 100.0

        println("""
                병렬 실행 성능 비교:
                Mutex
                - 평균: ${mutexAvg}ms, 
                - 최소: ${mutexMin}ms, 
                - 최대: ${mutexMax}ms

                Channel
                - 평균: ${channelAvg}ms, 
                - 최소: ${channelMin}ms, 
                - 최대: ${channelMax}ms
                
                평균 개선량: ${"%.2f".format(improvement)}%
                """.trimIndent())
    }
}