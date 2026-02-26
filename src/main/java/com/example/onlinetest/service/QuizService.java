package com.example.onlinetest.service;

import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import java.util.List;

public interface QuizService {
    QuizResponse createQuiz(QuizRequest request);
    QuizResponse getQuiz(Long id);
    List<QuizResponse> getAllQuizzes(String category, Boolean published, String tag);
    QuizResponse updateQuiz(Long id, QuizRequest request);
    void deleteQuiz(Long id);
}