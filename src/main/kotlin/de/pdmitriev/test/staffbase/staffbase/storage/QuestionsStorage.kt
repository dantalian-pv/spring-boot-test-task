package de.pdmitriev.test.staffbase.staffbase.storage

import de.pdmitriev.test.staffbase.staffbase.rest.RestNotFoundException
import de.pdmitriev.test.staffbase.staffbase.storage.model.PersistQuestion
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger

@Service
class QuestionsStorage {
    val idGenerator = AtomicInteger()
    val questions = mutableMapOf<Int, PersistQuestion>()

    fun allQuestions(limit: Int): List<PersistQuestion> {
        return questions.entries.map { it.value }.subList(0, questions.size.coerceAtMost(limit));
    }

    fun getQuestion(id: Int): PersistQuestion {
        val question = questions[id]
        return question ?: throw RestNotFoundException("No question with $id found")
    }

    fun addQuestion(title: String, content: String): PersistQuestion {
        val question = PersistQuestion(idGenerator.incrementAndGet(), title, content)
        questions[question.id] = question;
        return question;
    }

    fun editQuestion(id: Int, title: String, content: String): PersistQuestion {
        val question = getQuestion(id)
        question.title = title
        question.content = content
        return question
    }
}