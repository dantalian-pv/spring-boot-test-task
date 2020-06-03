package de.pdmitriev.test.staffbase.staffbase.rest

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class RestNotFoundException : ResponseStatusException {
    constructor(reason: String?, cause: Throwable?) : super(HttpStatus.NOT_FOUND, reason, cause) {}
    constructor(reason: String?) : super(HttpStatus.NOT_FOUND, reason) {}

    companion object {
        private const val serialVersionUID = 1L
    }
}