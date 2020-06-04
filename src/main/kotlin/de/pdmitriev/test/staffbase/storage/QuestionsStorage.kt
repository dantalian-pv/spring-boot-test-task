package de.pdmitriev.test.staffbase.storage

import de.pdmitriev.test.staffbase.storage.model.PersistQuestion
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class QuestionsStorage {
    protected val idGenerator = AtomicInteger()
    protected val questions = ConcurrentHashMap<Int, PersistQuestion>()

    fun allQuestions(limit: Int = -1): List<PersistQuestion> {
        val sizeLimit = if (limit == -1) questions.size else limit
        return questions.entries
                .map { it.value }
                .sortedByDescending { it.creationDate }
                .subList(0, questions.size.coerceAtMost(sizeLimit))
    }

    fun getQuestion(id: Int): PersistQuestion {
        val question = questions[id]
        return question ?: throw NoEntityFoundException("No question with id=$id found")
    }

    fun addQuestion(title: String, content: String): PersistQuestion {
        val question = PersistQuestion(idGenerator.incrementAndGet(),
                title,
                content,
                System.currentTimeMillis())
        questions[question.id] = question
        return question
    }

    fun editQuestion(id: Int, title: String, content: String): PersistQuestion {
        val question = getQuestion(id)
        question.title = title
        question.content = content
        return question
    }
}