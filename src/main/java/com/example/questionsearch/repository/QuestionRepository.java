package com.example.questionsearch.repository;

import com.example.questionsearch.entity.Question;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID> {

    @Query("select q from Question q order by length(q.value) desc")
    List<Question> findTopOfQuestionsByMaxLength(Pageable pageable);

    List<Question> findAllByValueStartingWith(String value);

}
