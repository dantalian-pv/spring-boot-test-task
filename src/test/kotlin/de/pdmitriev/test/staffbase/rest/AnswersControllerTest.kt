package de.pdmitriev.test.staffbase.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import de.pdmitriev.test.staffbase.rest.model.RestAnswer
import de.pdmitriev.test.staffbase.rest.model.RestQuestion
import de.pdmitriev.test.staffbase.storage.AnswersStorage
import de.pdmitriev.test.staffbase.storage.NoEntityFoundException
import de.pdmitriev.test.staffbase.storage.QuestionsStorage
import de.pdmitriev.test.staffbase.storage.model.PersistAnswer
import de.pdmitriev.test.staffbase.storage.model.PersistQuestion
import io.mockk.every
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest
internal class AnswersControllerTest(@Autowired val mockMvc: MockMvc, @Autowired val mapper: ObjectMapper) {

    @MockkBean
    lateinit var questionsStorage: QuestionsStorage

    @MockkBean
    lateinit var answersStorage: AnswersStorage

    companion object {
        const val ANSWERS_PATH = "/api/v1/answers"
    }

    @Test
    fun shouldReturnEmptyListForAll() {
        every { answersStorage.allAnswers(10) } returns listOf()
        mockMvc.perform(MockMvcRequestBuilders.get(ANSWERS_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.answers").isEmpty)
    }

    @Test
    fun shouldReturnAnswerById() {
        every { answersStorage.getAnswerById(0) } returns prepareAnswers(1, 1)[0]
        mockMvc.perform(MockMvcRequestBuilders.get("$ANSWERS_PATH/_id/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.questionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content").value("content0"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.creationDate").isNumber)
    }

    @Test
    fun shouldReturnAnswersByQuestionId() {
        every { answersStorage.answersToQuestion(1, 10) } returns prepareAnswers(1, 2)
        mockMvc.perform(MockMvcRequestBuilders.get("$ANSWERS_PATH/_question/1").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.answers").isArray)
                .andExpect(MockMvcResultMatchers.jsonPath("\$.answers[0].id").isNumber)
                .andExpect(MockMvcResultMatchers.jsonPath("\$.answers[0].questionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.answers[0].content").isString)
                .andExpect(MockMvcResultMatchers.jsonPath("\$.answers[0].creationDate").isNumber)
    }

    @Test
    fun `Error list answers when question does not exist`() {
        every { answersStorage.answersToQuestion(1, 10) } throws NoEntityFoundException("no 1")
        mockMvc.perform(MockMvcRequestBuilders.get("$ANSWERS_PATH/_question/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("no 1"))
    }

    @Test
    fun shouldAddNewAnswerToQuestion() {
        every { answersStorage.addAnswer(1, "content0") } returns prepareAnswers(1, 1)[0]
        mockMvc.perform(MockMvcRequestBuilders.post("$ANSWERS_PATH/_question/1")
                .content(mapper.writeValueAsString(RestAnswer(null, null, "content0", null)))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.id").isNumber)
                .andExpect(MockMvcResultMatchers.jsonPath("\$.questionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content").isString)
                .andExpect(MockMvcResultMatchers.jsonPath("\$.creationDate").isNumber)
    }

    @Test
    fun `Error add answer when question does not exist`() {
        every { answersStorage.addAnswer(1, "content0") } throws NoEntityFoundException("no 1")
        mockMvc.perform(MockMvcRequestBuilders.post("$ANSWERS_PATH/_question/1")
                .content(mapper.writeValueAsString(RestAnswer(null, null, "content0", null)))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("no 1"))
    }

    @Test
    fun shouldEditAnswer() {
        every { answersStorage.editAnswer(0, "content0") } returns prepareAnswers(1, 1)[0]
        mockMvc.perform(MockMvcRequestBuilders.put("$ANSWERS_PATH/_id/0")
                .content(mapper.writeValueAsString(RestAnswer(null, null, "content0", null)))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.id").isNumber)
                .andExpect(MockMvcResultMatchers.jsonPath("\$.questionId").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content").value("content0"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.creationDate").isNumber)
    }

    @Test
    fun `Error edit answer when answer does not exist`() {
        every { answersStorage.editAnswer(1, "content0") } throws NoEntityFoundException("no 1")
        mockMvc.perform(MockMvcRequestBuilders.put("$ANSWERS_PATH/_id/1")
                .content(mapper.writeValueAsString(RestAnswer(null, null, "content0", null)))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("no 1"))
    }

    private fun prepareAnswers(questionId: Int, size: Int): List<PersistAnswer> {
        return List(size) { PersistAnswer(it, questionId, "content$it", System.currentTimeMillis()) }
    }

}
