package com.example.onlinetest.repository;

import com.example.onlinetest.model.Quiz;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByCategoryIgnoreCase(String category);

    List<Quiz> findByIsPublished(Boolean published);

    @Query("SELECT q FROM Quiz q JOIN q.tags t WHERE t = :tag")
    List<Quiz> findByTag(@Param("tag") String tag);

    // Решение проблемы N+1 с помощью EntityGraph
    @EntityGraph(attributePaths = {"questions", "questions.answers"})
    @Query("SELECT q FROM Quiz q WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestionsAndAnswers(@Param("id") Long id);

    // Решение проблемы N+1 с помощью fetch join
    @Query("SELECT DISTINCT q FROM Quiz q LEFT JOIN FETCH q.questions WHERE q.id = :id")
    Optional<Quiz> findByIdWithQuestionsFetch(@Param("id") Long id);

    @EntityGraph(attributePaths = {"questions"})
    List<Quiz> findAll();
}