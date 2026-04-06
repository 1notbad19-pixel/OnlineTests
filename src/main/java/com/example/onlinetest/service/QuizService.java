package com.example.onlinetest.service;

import com.example.onlinetest.dto.FullQuizRequest;
import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface QuizService {

    QuizResponse createQuiz(QuizRequest request);

    QuizResponse createFullQuiz(FullQuizRequest request);

    QuizResponse createFullQuizWithoutTransaction(FullQuizRequest request);

    QuizResponse getQuiz(Long id);

    QuizResponse getQuizWithDetails(Long id);

    List<QuizResponse> getAllQuizzes(String category, Boolean published, String tag);

    Page<QuizResponse> getQuizzesWithFilters(String category, Boolean published,
        Integer minQuestions, Pageable pageable);

    Page<QuizResponse> getQuizzesWithFiltersNative(String category, Boolean published,
        Integer minQuestions, Pageable pageable);

    QuizResponse updateQuiz(Long id, QuizRequest request);

    void deleteQuiz(Long id);

    // Метод для инвалидации кэша
    void invalidateCache();
}