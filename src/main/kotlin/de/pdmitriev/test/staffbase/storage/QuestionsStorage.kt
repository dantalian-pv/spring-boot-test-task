package de.pdmitriev.test.staffbase.storage

import de.pdmitriev.test.staffbase.storage.exceptions.NoEntityFoundException
import de.pdmitriev.test.staffbase.storage.model.PersistQuestion
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger

@Service
class QuestionsStorage {
    private val idGenerator = AtomicInteger()
    private val questions = ConcurrentHashMap<Int, PersistQuestion>()

    fun allQuestions(limit: Int = 10): List<PersistQuestion> {
        val sizeLimit = if (limit < 0) throw IllegalArgumentException("limit must be >= 0") else limit
        return questions.entries
                .map { it.value }
                .sortedWith( Comparator{ item1, item2 ->
                    val dateCompare = item2.creationDate.compareTo(item1.creationDate)
                    if (dateCompare == 0) {
                        return@Comparator item2.id.compareTo(item1.id)
                    }
                    return@Comparator dateCompare
                })
                .subList(0, questions.size.coerceAtMost(sizeLimit))
    }

    fun getQuestion(id: Int): PersistQuestion {
        val question = questions[id]
        return question ?: throw NoEntityFoundException("No question with id=$id found")
    }

    fun addQuestion(title: String, content: String, user: String): PersistQuestion {
        val question = PersistQuestion(idGenerator.incrementAndGet(),
                title,
                content,
                user,
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