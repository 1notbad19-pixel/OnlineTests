package com.example.onlinetest.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
public class Question {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String text;

  @Column(nullable = false)
  private String type;

  @Column(name = "points", nullable = false)
  private Integer points = 1;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_id", nullable = false)
  private Quiz quiz;

  @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  private List<Answer> answers = new ArrayList<>();

  // Конструкторы
  public Question() {}

  public Question(String text, String type, Integer points) {
    this.text = text;
    this.type = type;
    this.points = points;
  }

  // Геттеры и сеттеры
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }

  public String getText() { return text; }
  public void setText(String text) { this.text = text; }

  public String getType() { return type; }
  public void setType(String type) { this.type = type; }

  public Integer getPoints() { return points; }
  public void setPoints(Integer points) { this.points = points; }

  public Quiz getQuiz() { return quiz; }
  public void setQuiz(Quiz quiz) { this.quiz = quiz; }

  public List<Answer> getAnswers() { return answers; }
  public void setAnswers(List<Answer> answers) { this.answers = answers; }
}