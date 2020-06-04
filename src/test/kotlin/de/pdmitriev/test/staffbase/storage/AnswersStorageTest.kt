package de.pdmitriev.test.staffbase.storage

import com.ninjasquad.springmockk.clear
import de.pdmitriev.test.staffbase.storage.model.PersistQuestion
import io.mockk.MockKAnnotations
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.impl.annotations.MockK
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class AnswersStorageTest {

    @MockK
    private lateinit var questionsStorage: QuestionsStorage

    private lateinit var answersStorage: AnswersStorage

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        answersStorage = AnswersStorage(questionsStorage)
    }

    @AfterEach
    fun shutDown() {
        clearMocks(questionsStorage)
    }

    @Test
    fun shouldReturnEmptyResult() {
        assertThat(answersStorage.allAnswers()).isEmpty()
    }

    @Test
    fun shouldReturnSingleAnswerInList() {
        // when
        prepareAnswers(1, 1)

        // then
        val answers = answersStorage.allAnswers()
        assertThat(answers).hasSize(1)
        assertThat(answers[0].questionId).isEqualTo(1)
        assertThat(answers[0].content).isEqualTo("content1")
    }

    @Test
    fun shouldReturnAnswersToMultipleQuestions() {
        // when
        prepareAnswers(1, 1)
        prepareAnswers(2, 2)

        // then
        val answers = answersStorage.allAnswers()
        assertThat(answers).hasSize(3)
        for (i in 2 downTo 0) {
            assertThat(answers[i].questionId).isIn(1, 2)
            assertThat(answers[i].content).contains("content")
        }
    }

    @Test
    fun shouldReturnAnswersToRequestedQuestion() {
        // when
        prepareAnswers(1, 2)

        // then
        val answers = answersStorage.answersToQuestion(1)
        assertThat(answers).hasSize(2)
        for (i in 1 downTo 0) {
            assertThat(answers[i].questionId).isEqualTo(1)
            assertThat(answers[i].content).contains("content")
        }
    }

    @Test
    fun shouldReturnAnswersToRequestedQuestionWithLimit() {
        // when
        prepareAnswers(1, 10)

        // then
        val answers = answersStorage.answersToQuestion(1, 2)
        assertThat(answers).hasSize(2)
        for (i in 1 downTo 0) {
            assertThat(answers[i].questionId).isEqualTo(1)
            assertThat(answers[i].content).contains("content")
        }
    }

    @Test
    fun `Error in answersToQuestion when question does not exist`() {
        // when
        every { questionsStorage.getQuestion(1) } throws NoEntityFoundException()

        // then
        Assertions.assertThrows(NoEntityFoundException::class.java) {
            answersStorage.answersToQuestion(1)
        }
    }

    @Test
    fun shouldReturnSingleAnswer() {
        // when
        prepareAnswers(1, 1)

        // then
        val answers = answersStorage.getAnswerById(1)
        assertThat(answers.questionId).isEqualTo(1)
        assertThat(answers.content).isEqualTo("content1")
    }

    @Test
    fun `Error in getAnswerById when answer does not exist`() {
        Assertions.assertThrows(NoEntityFoundException::class.java) {
            answersStorage.getAnswerById(1)
        }
    }

    @Test
    fun shouldCreateNewAnswer() {
        // given
        every { questionsStorage.getQuestion(1) } returns prepareQuestion(1)

        // then
        val answers = answersStorage.addAnswer(1, "content1")
        assertThat(answers.questionId).isEqualTo(1)
        assertThat(answers.content).isEqualTo("content1")
    }

    @Test
    fun `Error in addAnswer when question does not exist`() {
        // given
        every { questionsStorage.getQuestion(1) } throws NoEntityFoundException()

        // then
        Assertions.assertThrows(NoEntityFoundException::class.java) {
            answersStorage.addAnswer(1, "content1")
        }
    }

    @Test
    fun shouldEditExistingAnswer() {
        //when
        prepareAnswers(1, 1)

        // then
        val answer = answersStorage.editAnswer(1, "edit")
        assertThat(answer.id).isEqualTo(1)
        assertThat(answer.questionId).isEqualTo(1)
        assertThat(answer.content).isEqualTo("edit")
    }

    @Test
    fun `Error in editAnswer when answer does not exist`() {
        // then
        Assertions.assertThrows(NoEntityFoundException::class.java) {
            answersStorage.editAnswer(1, "edit")
        }
    }

    private fun prepareAnswers(questionId: Int, size: Int) {
        every { questionsStorage.getQuestion(questionId) } returns prepareQuestion(questionId)
        for (i in 1..size) {
            answersStorage.addAnswer(questionId, "content$i")
        }
    }

    private fun prepareQuestion(questionId: Int): PersistQuestion {
        return PersistQuestion(questionId, "title$questionId", "content$questionId", questionId.toLong())
    }
}
