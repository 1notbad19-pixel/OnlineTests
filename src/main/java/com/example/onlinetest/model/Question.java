package com.example.onlinetest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
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

    public Question() {
    }

    public Question(String text, String type, Integer points) {
        this.text = text;
        this.type = type;
        this.points = points;
    }

  // Геттеры
    public Long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getType() {
        return type;
    }

    public Integer getPoints() {
        return points;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

  // Сеттеры
    public void setId(Long id) {
        this.id = id;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }
}