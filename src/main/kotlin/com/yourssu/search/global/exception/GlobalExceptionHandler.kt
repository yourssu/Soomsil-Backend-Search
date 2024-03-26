package com.yourssu.search.global.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.SocketTimeoutException

@RestControllerAdvice
class GlobalExceptionHandler {

    companion object {
        private const val SOCKET_TIMEOUT_EXCEPTION = "Search-001"
        private const val ELASTIC_CONNECTION_EXCEPTION = "Search-002"
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SocketTimeoutException::class)
    fun handleSocketTimeoutException(exception: SocketTimeoutException): ErrorResponse {
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, SOCKET_TIMEOUT_EXCEPTION, exception.message!!)
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ElasticConnectionException::class)
    fun handleElasticConnectionException(exception: ElasticConnectionException): ErrorResponse {
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, ELASTIC_CONNECTION_EXCEPTION, exception.message!!)
    }
}
