package com.example.onlinetest.repository;

import com.example.onlinetest.model.Quiz;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class QuizRepository {
    private final Map<Long, Quiz> quizzes = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public Quiz save(Quiz quiz) {
        if (quiz.getId() == null) quiz.setId(idGenerator.getAndIncrement());
        quizzes.put(quiz.getId(), quiz);
        return quiz;
    }

    public Optional<Quiz> findById(Long id) {
        return Optional.ofNullable(quizzes.get(id));
    }

    public List<Quiz> findAll() {
        return List.copyOf(quizzes.values());
    }

    public List<Quiz> findByCategory(String category) {
        return quizzes.values().stream()
                .filter(q -> category.equalsIgnoreCase(q.getCategory()))
                .toList();
    }

    public List<Quiz> findByPublishedStatus(Boolean published) {
        return quizzes.values().stream()
                .filter(q -> published.equals(q.getIsPublished()))
                .toList();
    }

    public List<Quiz> findByTag(String tag) {
        return quizzes.values().stream()
                .filter(q -> q.getTags() != null && q.getTags().contains(tag))
                .toList();
    }

    public void deleteById(Long id) {
        quizzes.remove(id);
    }

    public boolean existsById(Long id) {
        return quizzes.containsKey(id);
    }
}