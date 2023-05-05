package com.example.questionsearch.service;

import java.util.List;

public interface QuestionSearchService {

    List<String> getTopOfQuestionsByMaxLength(Integer count);

    List<String> getTheMostRelatedQuestions(String question, Integer count);

}
