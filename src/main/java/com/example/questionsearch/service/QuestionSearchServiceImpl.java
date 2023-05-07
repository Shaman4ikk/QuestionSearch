package com.example.questionsearch.service;

import com.example.questionsearch.entity.Question;
import com.example.questionsearch.repository.QuestionRepository;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.*;

@Service
@RequiredArgsConstructor
public class QuestionSearchServiceImpl implements QuestionSearchService {

    private final QuestionRepository repository;
    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(5);

    private static final String REGEX_FOR_WORD = "[\\p{Punct}\\s]+";

    @Override
    public List<String> getTopOfQuestionsByMaxLength(Integer count) {
        return repository
                .findTopOfQuestionsByMaxLength(PageRequest.of(0, count, Sort.unsorted()))
                .stream().map(Question::getValue).toList();
    }

    @Override
    public List<String> getTheMostRelatedQuestions(String question, Integer count) {
        List<String> splitQuestion = Arrays.stream(question.split(REGEX_FOR_WORD))
                .toList();
        String firstWord = splitQuestion.get(0);
        List<String> candidates = repository.findAllByValueStartingWith(firstWord)
                .stream().map(Question::getValue).toList();

        splitQuestion = splitQuestion.stream().filter(s1 -> s1.length() > 3).toList();

        Map<String, Double> relatedWordsMap = new HashMap<>();

        if (!CollectionUtils.isEmpty(splitQuestion)) {
            relatedWordsMap = getRelatedWords(splitQuestion, candidates);
        }

        if (relatedWordsMap.isEmpty() || relatedWordsMap.values().stream().allMatch(val -> val.equals(0.0))) {
            Question newQuestion = new Question();
            newQuestion.setValue(question);
            repository.save(newQuestion);

            return new ArrayList<>();
        }

        List<String> theMostRelated = relatedWordsMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .map(Map.Entry::getKey).toList();
        return theMostRelated.subList(0, count > theMostRelated.size() ? theMostRelated.size() : count);
    }

    @PreDestroy
    private void preDestroy() {
        executor.shutdown();
    }

    private Double calculateRelatedWords(List<String> question, List<String> splitQuestion) {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
        int res = 0;

        for (String questionWord : splitQuestion) {
            int threshold = questionWord.length() / 3;
            for (String questionWordFromDb : question) {
                int distance = levenshteinDistance.apply(questionWord, questionWordFromDb);

                if (distance <= threshold) {
                    res++;
                    break;
                }
            }
        }

        return res > 0 ? (double) res / question.size() : 0.0;
    }

    private Map<String, Double> getRelatedWords(List<String> question, List<String> candidates) {
        Map<String, Double> relatedWordsMap = new HashMap<>();

        for (String s : candidates) {
            List<String> splitString = Arrays.stream(s.split(REGEX_FOR_WORD))
                    .toList().stream().filter(s1 -> s1.length() > 3).toList();

            Callable<Double> task = () -> calculateRelatedWords(splitString, question);
            Future<Double> future = executor.submit(task);

            try {
                Double val = future.get();
                relatedWordsMap.put(s, val);
            } catch (ExecutionException | InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        return relatedWordsMap;
    }


}
