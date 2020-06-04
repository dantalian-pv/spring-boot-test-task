package de.pdmitriev.test.staffbase.storage

import de.pdmitriev.test.staffbase.storage.exceptions.NoEntityFoundException
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class QuestionsStorageTest {

    private lateinit var questionsStorage: QuestionsStorage

    @BeforeEach
    fun init() {
        questionsStorage = QuestionsStorage()
    }

    @Test
    fun shouldReturnEmptyList() {
        assertThat(questionsStorage.allQuestions()).isEmpty()
    }

    @Test
    fun shouldReturnEmptyListForBigLimit() {
        assertThat(questionsStorage.allQuestions(100)).isEmpty()
    }

    @Test
    fun shouldReturnOneQuestionWhenOneAdded() {
        // when
        addQuestions(1)

        // then
        val allQuestions = questionsStorage.allQuestions()
        assertThat(allQuestions).hasSize(1)
        val question = allQuestions[0]
        assertThat(question.id).isPositive()
        assertThat(question.title).isEqualTo("title1")
        assertThat(question.content).isEqualTo("content1")
    }

    @Test
    fun shouldReturnAllAddedQuestions() {
        // when
        val size = 20
        addQuestions(size)

        // then
        val allQuestions = questionsStorage.allQuestions(size)
        assertThat(allQuestions).hasSize(size)
    }

    @Test
    fun shouldReturnAllAddedQuestionsInParticularOrder() {
        // when
        val size = 20
        addQuestions(size)

        // then
        val allQuestions = questionsStorage.allQuestions(size)
        assertThat(allQuestions).hasSize(size)
        var i = size
        for (nextQuestion in allQuestions) {
            assertThat(nextQuestion.title).isEqualTo("title$i")
            assertThat(nextQuestion.content).isEqualTo("content$i")
            i--
        }
    }

    @Test
    fun shouldReturnAddedQuestionsInDescendingByTime() {
        // when
        val size = 20
        addQuestions(size)

        // then
        val allQuestions = questionsStorage.allQuestions(size)
        assertThat(allQuestions).hasSize(size)
        val creationDateList = allQuestions.map { it.creationDate }
        val sortedDatesList = creationDateList.sortedByDescending { it }
        assertThat(creationDateList).isEqualTo(sortedDatesList)
    }

    @Test
    fun shouldReturnLimitedAddedQuestions() {
        // when
        addQuestions(20)

        // then
        val allQuestions = questionsStorage.allQuestions(10)
        assertThat(allQuestions).hasSize(10)
    }

    @Test
    fun shouldReturnOneQuestionById() {
        // when
        val addedQuestion = questionsStorage.addQuestion("title1", "content1", "user1")

        // then
        val question = questionsStorage.getQuestion(addedQuestion.id)
        assertThat(question.id).isEqualTo(addedQuestion.id)
        assertThat(question.title).isEqualTo("title1")
        assertThat(question.content).isEqualTo("content1")
    }

    @Test
    fun shouldThrowExceptionForGettingNotExistingId() {
        Assertions.assertThrows(NoEntityFoundException::class.java) {
            questionsStorage.getQuestion(1)
        }
    }

    @Test
    fun shouldThrowExceptionForEditingNotExistingId() {
        Assertions.assertThrows(NoEntityFoundException::class.java) {
            questionsStorage.editQuestion(1, "t1", "c1")
        }
    }

    @Test
    fun shouldEditAddedQuestion() {
        // when
        val addedQuestion = questionsStorage.addQuestion("title1", "content1", "user1")

        // then
        val question = questionsStorage.editQuestion(addedQuestion.id, "title2", "content2")
        assertThat(question.id).isEqualTo(addedQuestion.id)
        assertThat(question.title).isEqualTo("title2")
        assertThat(question.content).isEqualTo("content2")
    }

    private fun addQuestions(size: Int) {
        for (i in 1..size) {
            questionsStorage.addQuestion("title$i", "content$i", "user1")
        }
    }
}
