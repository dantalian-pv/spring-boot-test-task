package de.pdmitriev.test.staffbase.rest

import de.pdmitriev.test.staffbase.rest.model.RestAnswer
import de.pdmitriev.test.staffbase.rest.model.RestAnswerList
import de.pdmitriev.test.staffbase.rest.model.RestQuestionList
import de.pdmitriev.test.staffbase.storage.AnswersStorage
import de.pdmitriev.test.staffbase.storage.model.PersistAnswer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/answers")
class AnswersController(@Autowired private val answersStorage: AnswersStorage) {

    @GetMapping()
    @ResponseBody
    fun allAnswers(@RequestParam(defaultValue = "10") limit: Int): RestAnswerList {
        return RestAnswerList(answersStorage.allAnswers(limit).map { it.rest() })
    }

    @GetMapping("/_id/{id}")
    @ResponseBody
    fun getAnswerById(@PathVariable id: Int): RestAnswer {
        return answersStorage.getAnswerById(id).rest()
    }

    @GetMapping("/_question/{questionId}")
    @ResponseBody
    fun listAnswersByQuestionId(@PathVariable questionId: Int,
                                @RequestParam(defaultValue = "10") limit: Int): RestAnswerList {
        return RestAnswerList(answersStorage.answersToQuestion(questionId, limit).map { it.rest() })
    }

    fun PersistAnswer.rest() = RestAnswer(
            id,
            questionId,
            content,
            creationDate
    )
}