package de.pdmitriev.test.staffbase.rest.model

data class RestAnswer(
        val id: Int?,
        val questionId: Int?,
        val content: String,
        val creationDate: Long?
)