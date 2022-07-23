package com.codewithevans.blog.exceptions

import com.codewithevans.blog.dtos.ErrorDto
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus.*
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
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

    @ExceptionHandler(Unauthorized::class)
    fun handleResourceExists(e: Unauthorized): ResponseEntity<ErrorDto> {
        logger.warn("${e.message} | ${UNAUTHORIZED.value()}")
        val errorDto = ErrorDto(UNAUTHORIZED.name, e.message, LocalDateTime.now())
        return ResponseEntity(errorDto, UNAUTHORIZED)
    }

    @ExceptionHandler(InternalError::class)
    fun handleInternalError(e: InternalError): ResponseEntity<ErrorDto> {
        logger.error("${e.message} | ${INTERNAL_SERVER_ERROR.value()}")
        val errorDto = ErrorDto(INTERNAL_SERVER_ERROR.name, e.message, LocalDateTime.now())
        return ResponseEntity(errorDto, INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleException(e: WebExchangeBindException): ResponseEntity<Map<String, String?>>? {
        val errors: MutableMap<String, String?> = HashMap()
        e.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.defaultMessage
            errors[fieldName] = errorMessage
        }
        return ResponseEntity.badRequest().body(errors)
    }
}