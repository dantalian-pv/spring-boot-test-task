package de.pdmitriev.test.staffbase.rest.model

data class RestQuestion(
        val id: Int?,
        val title: String,
        val content: String,
        val creationDate: Long?)