package com.example.onlinetest.service;

import com.example.onlinetest.dto.FullQuizRequest;
import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface QuizService {

    QuizResponse createFullQuiz(FullQuizRequest request);

    QuizResponse createFullQuizWithoutTransaction(FullQuizRequest request);

    QuizResponse getQuiz(Long id);

    QuizResponse updateQuiz(Long id, QuizRequest request);

    QuizResponse getQuizWithDetails(Long id);

    QuizResponse createQuiz(QuizRequest request, Long userId);

    List<QuizResponse> getAllQuizzes(String category, Boolean published, String tag);

    Page<QuizResponse> getQuizzesWithFilters(String category, Boolean published,
        Integer minQuestions, Pageable pageable);

    Page<QuizResponse> getQuizzesWithFiltersNative(String category, Boolean published,
        Integer minQuestions, Pageable pageable);

    List<QuizResponse> createQuizzesBulk(List<QuizRequest> requests);
    List<QuizResponse> createQuizzesBulkWithoutTransaction(List<QuizRequest> requests);

    void deleteQuiz(Long id);
    int deleteByCategory(String category);
    int deleteByPublishedStatus(Boolean published);
    int deleteByTag(String tagName);
    int deleteByMinQuestions(int minQuestions);
    long deleteAllQuizzes();

    void invalidateCache();
}