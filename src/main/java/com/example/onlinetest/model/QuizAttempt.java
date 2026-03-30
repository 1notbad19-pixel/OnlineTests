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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quiz_attempts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuizAttempt {

    @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @ToString.Exclude
  private User user;

    @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_id", nullable = false)
  @ToString.Exclude
  private Quiz quiz;

    @Column(name = "start_time")
  private LocalDateTime startTime;

    @Column(name = "end_time")
  private LocalDateTime endTime;

    @Column(name = "score")
  private Integer score;

    @Column(name = "passed")
  private Boolean passed;

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @ToString.Exclude
  private List<UserAnswer> userAnswers = new ArrayList<>();
}