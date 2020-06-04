package de.pdmitriev.test.staffbase.rest

import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import de.pdmitriev.test.staffbase.rest.model.RestQuestion
import de.pdmitriev.test.staffbase.storage.AnswersStorage
import de.pdmitriev.test.staffbase.storage.exceptions.NoEntityFoundException
import de.pdmitriev.test.staffbase.storage.QuestionsStorage
import de.pdmitriev.test.staffbase.storage.model.PersistQuestion
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@WebMvcTest
internal class QuestionsControllerTest(@Autowired val mockMvc: MockMvc, @Autowired val mapper: ObjectMapper) {

    @MockkBean
    lateinit var questionsStorage: QuestionsStorage

    @MockkBean
    lateinit var answersStorage: AnswersStorage

    companion object {
        const val QUESTIONS_PATH = "/api/v1/questions"
    }

    @Test
    fun shouldReturnEmptyList() {
        every { questionsStorage.allQuestions(10) } returns listOf()
        mockMvc.perform(MockMvcRequestBuilders.get(QUESTIONS_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.questions").isEmpty)
    }

    @Test
    fun shouldReturnSingleElementInList() {
        every { questionsStorage.allQuestions(10) } returns addQuestions(1)
        mockMvc.perform(MockMvcRequestBuilders.get(QUESTIONS_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.questions[0].id").value(0))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.questions[0].title").value("title0"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.questions[0].content").value("content0"))
    }

    @Test
    fun shouldGetSingleElement() {
        every { questionsStorage.getQuestion(1) } returns createQuestion(1)
        mockMvc.perform(MockMvcRequestBuilders.get("$QUESTIONS_PATH/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.title").value("title1"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content").value("content1"))
    }

    @Test
    fun shouldAddSingleElement() {
        every { questionsStorage.addQuestion("title1", "content1") } returns createQuestion(1)
        mockMvc.perform(MockMvcRequestBuilders.post(QUESTIONS_PATH)
                .content(mapper.writeValueAsString(RestQuestion(null, "title1", "content1", null)))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.title").value("title1"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content").value("content1"))
    }

    @Test
    fun `Error when element does not exist`() {
        every { questionsStorage.getQuestion(1) } throws NoEntityFoundException("no 1")
        mockMvc.perform(MockMvcRequestBuilders.get("$QUESTIONS_PATH/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNotFound)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.status").value(404))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("no 1"))
    }

    @Test
    fun shouldEditSingleElementById() {
        val question1 = PersistQuestion(1, "title2", "content2", System.currentTimeMillis())
        every { questionsStorage.editQuestion(1,"title2", "content2") } returns question1
        mockMvc.perform(MockMvcRequestBuilders.put("$QUESTIONS_PATH/1")
                .content(mapper.writeValueAsString(RestQuestion(null, "title2", "content2", null)))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(1))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.title").value("title2"))
                .andExpect(MockMvcResultMatchers.jsonPath("\$.content").value("content2"))
    }

    private fun createQuestion(id: Int): PersistQuestion {
        return PersistQuestion(id,
        "title$id",
        "content$id",
        System.currentTimeMillis())
    }

    private fun addQuestions(size: Int): List<PersistQuestion> {
        return List(size) { PersistQuestion(it, "title$it", "content$it", System.currentTimeMillis()) }
    }
}
