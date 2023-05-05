package com.example.questionsearch.controller;

import com.example.questionsearch.service.QuestionSearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "QuestionController")
public class QuestionController {

    private final QuestionSearchService service;

    @GetMapping("/topQuestions")
    public List<String> getTopOfQuestionsByMaxLength(@Min(1) Integer count) {
        return service.getTopOfQuestionsByMaxLength(count);
    }

    @GetMapping("/getRelated")
    public List<String> getTheMostRelatedQuestions(@NotBlank String question,
                                                   @Min(1) Integer count) {
        return service.getTheMostRelatedQuestions(question, count);
    }
}
