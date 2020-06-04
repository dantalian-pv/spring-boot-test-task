package de.pdmitriev.test.staffbase.rest

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.http.HttpStatus

class RestExceptionBody(
        @JsonProperty("status") var status: Int,
        @JsonProperty("message") var message: String?) {

    constructor(status: HttpStatus, message: String?) : this(status.value(), message)
}