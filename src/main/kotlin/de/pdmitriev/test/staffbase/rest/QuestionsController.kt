package de.pdmitriev.test.staffbase.rest

import de.pdmitriev.test.staffbase.rest.model.RestQuestion
import de.pdmitriev.test.staffbase.rest.model.RestQuestionList
import de.pdmitriev.test.staffbase.storage.NoEntityFoundException
import de.pdmitriev.test.staffbase.storage.QuestionsStorage
import de.pdmitriev.test.staffbase.storage.model.PersistQuestion
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/questions")
class QuestionsController(private val questionsStorage: QuestionsStorage) {

    @GetMapping()
    @ResponseBody
    fun allQuestions(@RequestParam(defaultValue = "10") limit: Int): RestQuestionList {
        return RestQuestionList(questionsStorage.allQuestions(limit).map { it.rest() })
    }

    @GetMapping("/{id}")
    @ResponseBody
    fun getQuestion(@PathVariable id: Int): RestQuestion {
        try {
            return questionsStorage.getQuestion(id).rest()
        } catch (e: NoEntityFoundException) {
            throw RestNotFoundException("No question found with $id", e)
        }

    }

    @PostMapping(consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @ResponseBody
    fun addQuestion(@RequestBody restQuestion: RestQuestion): RestQuestion {
        return questionsStorage.addQuestion(restQuestion.title, restQuestion.content).rest()
    }

    @PutMapping("/{id}", consumes = arrayOf(MediaType.APPLICATION_JSON_VALUE))
    @ResponseBody
    fun editQuestion(@PathVariable id: Int, @RequestBody restQuestion: RestQuestion): RestQuestion {
        try {
            return questionsStorage.editQuestion(id, restQuestion.title, restQuestion.content).rest()
        } catch (e: NoEntityFoundException) {
            throw RestNotFoundException("No question found with $id", e)
        }
    }

    fun PersistQuestion.rest() = RestQuestion(
            id,
            title,
            content,
            creationDate
    )

}
