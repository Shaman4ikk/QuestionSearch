package com.example.questionsearch.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name = "question")
@Data
public class Question {

    @Id
    private final UUID id = UUID.randomUUID();

    @Column(name = "value")
    private String value;

}
