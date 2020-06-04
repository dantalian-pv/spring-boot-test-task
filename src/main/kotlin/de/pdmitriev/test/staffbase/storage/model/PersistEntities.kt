package de.pdmitriev.test.staffbase.storage.model

class PersistQuestion(
		var id: Int,
		var title: String,
		var content: String,
		var creationDate: Long
)

class PersistAnswer(
		var id: Int,
		var questionId: Int,
		var content: String,
		var creationDate: Long
)