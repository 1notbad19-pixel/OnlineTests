package com.example.onlinetest.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "user_answers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserAnswer {

    @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "attempt_id", nullable = false)
  @ToString.Exclude
  private QuizAttempt attempt;

    @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "question_id", nullable = false)
  @ToString.Exclude
  private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "answer_id")
  @ToString.Exclude
  private Answer selectedAnswer;

    @Column(name = "text_answer", columnDefinition = "TEXT")
  private String textAnswer;

    @Column(name = "is_correct")
  private Boolean isCorrect;
}