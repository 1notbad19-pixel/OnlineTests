package com.example.onlinetest.repository;

import com.example.onlinetest.model.QuizAttempt;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByUserId(Long userId);

    List<QuizAttempt> findByQuizId(Long quizId);

    @EntityGraph(attributePaths = {"userAnswers", "userAnswers.question", "userAnswers.selectedAnswer"})
  @Query("SELECT a FROM QuizAttempt a WHERE a.id = :id")
  Optional<QuizAttempt> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT COUNT(a) FROM QuizAttempt a WHERE a.user.id = :userId AND a.quiz.id = :quizId")
  long countAttemptsByUserAndQuiz(@Param("userId") Long userId, @Param("quizId") Long quizId);
}