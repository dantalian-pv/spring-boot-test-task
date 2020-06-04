package de.pdmitriev.test.staffbase.rest

import de.pdmitriev.test.staffbase.storage.NoEntityFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

internal class RestResponseExceptionHandlerTest {

    lateinit var errorHandler: RestResponseExceptionHandler

    @BeforeEach
    fun setUp() {
        errorHandler = RestResponseExceptionHandler()
    }

    @Test
    fun shouldReturnInternalErrorWhenRestInternalError() {
        val responseEntity = errorHandler.handleAccessDeniedException(RestInternalErrorException("internal"), null)
        checkResponse(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR, "internal")
    }

    @Test
    fun shouldReturnNotFoundErrorWhenNoEntityFoundException() {
        val responseEntity = errorHandler.handleAccessDeniedException(NoEntityFoundException("no entity"), null)
        checkResponse(responseEntity, HttpStatus.NOT_FOUND, "no entity")
    }

    @Test
    fun shouldReturnInternalErrorWhenUnknownTypeException() {
        val responseEntity = errorHandler.handleAccessDeniedException(Exception("ex"), null)
        checkResponse(responseEntity, HttpStatus.INTERNAL_SERVER_ERROR, "ex")
    }

    private fun checkResponse(responseEntity: ResponseEntity<Any>, status: HttpStatus, message: String) {
        assertThat(responseEntity.statusCode).isEqualTo(status)
        assertThat(responseEntity.body).isInstanceOf(RestExceptionBody::class.java)
        val body = responseEntity.body as RestExceptionBody
        assertThat(body.message).isEqualTo(message)
    }
}
