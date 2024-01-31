package com.yourssu.search.global.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.net.SocketTimeoutException

@RestControllerAdvice
class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(SocketTimeoutException::class)
    fun handleSocketTimeoutException(exception: SocketTimeoutException): ErrorResponse {
        return ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, errCode = "Search-001", exception.message!!)
    }
}