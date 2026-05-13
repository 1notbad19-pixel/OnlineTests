package com.example.onlinetest.service;

import com.example.onlinetest.dto.QuestionRequest;
import com.example.onlinetest.dto.QuestionResponse;
import java.util.List;

public interface QuestionService {

  QuestionResponse createQuestion(QuestionRequest request);

  QuestionResponse getQuestion(Long id);

  List<QuestionResponse> getAllQuestions();

  List<QuestionResponse> getQuestionsByQuizId(Long quizId);

  QuestionResponse updateQuestion(Long id, QuestionRequest request);

  void deleteQuestion(Long id);
}