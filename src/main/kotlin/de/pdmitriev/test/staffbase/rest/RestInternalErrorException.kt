package de.pdmitriev.test.staffbase.rest

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class RestInternalErrorException : ResponseStatusException {
    constructor(aReason: String?, aCause: Throwable?) : super(HttpStatus.INTERNAL_SERVER_ERROR, aReason, aCause) {}
    constructor(aReason: String?) : super(HttpStatus.INTERNAL_SERVER_ERROR, aReason) {}

    companion object {
        private const val serialVersionUID = 1L
    }
}