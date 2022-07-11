package com.codewithevans.blog.exceptions

import com.codewithevans.blog.dtos.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime

@ControllerAdvice
class PostServiceExceptionHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @ExceptionHandler(ResourceNotFound::class)
    fun handleResourceNotFound(e: ResourceNotFound): ResponseEntity<ErrorDto> {
        logger.warn("${e.message} | ${NOT_FOUND.value()}")
        val errorDto = ErrorDto(NOT_FOUND.name, e.message, LocalDateTime.now())
        return ResponseEntity(errorDto, NOT_FOUND)
    }

    @ExceptionHandler(ResourceExists::class)
    fun handleResourceExists(e: ResourceExists): ResponseEntity<ErrorDto> {
        logger.warn("${e.message} | ${CONFLICT.value()}")
        val errorDto = ErrorDto(CONFLICT.name, e.message, LocalDateTime.now())
        return ResponseEntity(errorDto, CONFLICT)
    }

    @ExceptionHandler(InternalError::class)
    fun handleInternalError(e: InternalError): ResponseEntity<ErrorDto>{
        logger.error("${e.message} | ${INTERNAL_SERVER_ERROR.value()}")
        val errorDto = ErrorDto(INTERNAL_SERVER_ERROR.name, e.message, LocalDateTime.now())
        return ResponseEntity(errorDto, INTERNAL_SERVER_ERROR)
    }
}