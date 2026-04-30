package com.example.onlinetest.repository;

import com.example.onlinetest.model.Quiz;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @EntityGraph(attributePaths = {"tags", "questions", "questions.answers"})
  @Query("SELECT q FROM Quiz q WHERE q.id = :id")
  Optional<Quiz> findByIdWithAllDetails(@Param("id") Long id);

    @Query("SELECT DISTINCT q FROM Quiz q " +
        "LEFT JOIN FETCH q.tags t " +
        "LEFT JOIN FETCH q.questions qs " +
        "WHERE (:category IS NULL OR q.category = :category) " +
        "AND (:published IS NULL OR q.isPublished = :published) " +
        "AND (:minQuestions IS NULL OR SIZE(q.questions) >= :minQuestions)")
  Page<Quiz> findQuizzesWithFilters(@Param("category") String category,
        @Param("published") Boolean published,
        @Param("minQuestions") Integer minQuestions,
        Pageable pageable);

    @Query(value = "SELECT DISTINCT q.* FROM quizzes q " +
        "LEFT JOIN questions qs ON q.id = qs.quiz_id " +
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