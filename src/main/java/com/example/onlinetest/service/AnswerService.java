package com.example.onlinetest.service;

import com.example.onlinetest.dto.AnswerRequest;
import com.example.onlinetest.dto.AnswerResponse;
import java.util.List;

public interface AnswerService {

  AnswerResponse createAnswer(AnswerRequest request);

  AnswerResponse getAnswer(Long id);

  List<AnswerResponse> getAllAnswers();

  List<AnswerResponse> getAnswersByQuestionId(Long questionId);

  AnswerResponse updateAnswer(Long id, AnswerRequest request);

  void deleteAnswer(Long id);
}