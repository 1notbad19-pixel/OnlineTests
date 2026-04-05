package com.example.onlinetest.repository;

import com.example.onlinetest.model.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByCategoryIgnoreCase(String category);

    List<Quiz> findByIsPublished(Boolean published);

    @Query("SELECT q FROM Quiz q JOIN q.tags t WHERE t = :tag")
    List<Quiz> findByTag(@Param("tag") String tag);

    // 1. Сложный JPQL запрос с фильтрацией по вложенной сущности
    @Query("SELECT DISTINCT q FROM Quiz q " +
        "LEFT JOIN q.questions qs " +
        "LEFT JOIN qs.answers a " +
        "WHERE (:category IS NULL OR q.category = :category) " +
        "AND (:published IS NULL OR q.isPublished = :published) " +
        "AND (:minQuestions IS NULL OR SIZE(q.questions) >= :minQuestions)")
    Page<Quiz> findQuizzesWithFilters(@Param("category") String category,
        @Param("published") Boolean published,
        @Param("minQuestions") Integer minQuestions,
        Pageable pageable);

    // 2. Native SQL запрос
    @Query(value = "SELECT DISTINCT q.* FROM quizzes q " +
        "LEFT JOIN questions qs ON q.id = qs.quiz_id " +
        "LEFT JOIN answers a ON qs.id = a.question_id " +
        "WHERE (:category IS NULL OR q.category = :category) " +
        "AND (:published IS NULL OR q.is_published = :published) " +
        "AND (:minQuestions IS NULL OR (SELECT COUNT(*) FROM questions WHERE quiz_id = q.id) >= :minQuestions)",
        countQuery = "SELECT COUNT(DISTINCT q.id) FROM quizzes q",
        nativeQuery = true)
    Page<Quiz> findQuizzesWithFiltersNative(@Param("category") String category,
        @Param("published") Boolean published,
        @Param("minQuestions") Integer minQuestions,
        Pageable pageable);
}