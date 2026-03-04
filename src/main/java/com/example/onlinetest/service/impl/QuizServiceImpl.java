package com.example.onlinetest.service.impl;

import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import com.example.onlinetest.mapper.QuizMapper;
import com.example.onlinetest.model.Quiz;
import com.example.onlinetest.repository.QuizRepository;
import com.example.onlinetest.service.QuizService;  // <- ЭТОТ ИМПОРТ ОТСУТСТВОВАЛ!

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Реализация сервиса для управления тестами.
 * Предоставляет бизнес-логику для работы с тестами.
 */
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {  // <- Теперь QuizService распознается

    private final QuizRepository quizRepository;

    @Override
  public QuizResponse createQuiz(QuizRequest request) {
        Quiz quiz = QuizMapper.toEntity(request);
        Quiz savedQuiz = quizRepository.save(quiz);
        return QuizMapper.toResponse(savedQuiz);
    }

    @Override
  public QuizResponse getQuiz(Long id) {
        return quizRepository.findById(id)
        .map(QuizMapper::toResponse)
        .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));
    }

    @Override
  public List<QuizResponse> getAllQuizzes(String category, Boolean published, String tag) {
        List<Quiz> quizzes;

        if (category != null) {
            quizzes = quizRepository.findByCategory(category);
        } else if (published != null) {
            quizzes = quizRepository.findByPublishedStatus(published);
        } else if (tag != null) {
            quizzes = quizRepository.findByTag(tag);
        } else {
            quizzes = quizRepository.findAll();
        }

        return quizzes.stream()
        .map(QuizMapper::toResponse)
        .toList();
    }

    @Override
  public QuizResponse updateQuiz(Long id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));

        QuizMapper.update(quiz, request);
        Quiz updatedQuiz = quizRepository.save(quiz);

        return QuizMapper.toResponse(updatedQuiz);
    }

    @Override
  public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new IllegalArgumentException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }
}