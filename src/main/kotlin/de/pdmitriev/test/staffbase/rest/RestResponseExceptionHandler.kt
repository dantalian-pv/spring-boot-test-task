package de.pdmitriev.test.staffbase.rest

import de.pdmitriev.test.staffbase.storage.NoEntityFoundException
import de.pdmitriev.test.staffbase.storage.StorageException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.context.request.WebRequest
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler


@ControllerAdvice
class RestResponseExceptionHandler : ResponseEntityExceptionHandler() {
    @ExceptionHandler(ResponseStatusException::class, StorageException::class)
    fun handleAccessDeniedException(
            ex: Exception, request: WebRequest?): ResponseEntity<Any> {
        val printStack: Boolean
        val body = when (ex) {
            is ResponseStatusException -> {
                printStack = ex.status.is5xxServerError
                RestExceptionBody(ex.status, ex.reason)
            }
            is NoEntityFoundException -> {
                printStack = false
                RestExceptionBody(HttpStatus.NOT_FOUND, ex.message)
            }
            else -> {
                printStack = true
                RestExceptionBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.message)
            }
        }
        val path = (request as ServletWebRequest).request.requestURI
        if (printStack) {
            logger.error("REST Error ${body.status} occurred in $path", ex)
        } else {
            logger.warn("User Error ${body.status} occurred in $path message: ${ex.message}")
        }
        return ResponseEntity(body, HttpHeaders(), HttpStatus.valueOf(body.status))
    }
}