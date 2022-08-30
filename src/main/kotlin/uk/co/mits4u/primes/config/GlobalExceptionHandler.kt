package uk.co.mits4u.primes.config

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.RestControllerAdvice
import uk.co.mits4u.primes.api.pojos.ExceptionData

@RestControllerAdvice
class GlobalExceptionHandler {

    private val LOGGER = LoggerFactory.getLogger(this.javaClass)

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleUserFacingException(e: IllegalArgumentException): ResponseEntity<ExceptionData> {
        LOGGER.error(e.message, e)
        return ResponseEntity(ExceptionData(e.message!!), HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [Exception::class])
    @ResponseBody
    fun handleGenericException(e: Exception): ResponseEntity<ExceptionData> {
        LOGGER.error(e.message, e)
        return ResponseEntity<ExceptionData>(ExceptionData(e.message!!), HttpStatus.INTERNAL_SERVER_ERROR)
    }

}