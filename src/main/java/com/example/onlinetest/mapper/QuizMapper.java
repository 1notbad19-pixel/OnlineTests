package com.example.onlinetest.mapper;

import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import com.example.onlinetest.model.Quiz;

import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;

/**
 * Утилитарный класс для преобразования между сущностью Quiz и DTO.
 */
@UtilityClass
public class QuizMapper {

  /**
   * Преобразует QuizRequest в сущность Quiz.
   *
   * @param request DTO с данными для создания теста
   * @return сущность Quiz
   */
    public Quiz toEntity(QuizRequest request) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setCategory(request.category());
        quiz.setTimeLimitMinutes(request.timeLimitMinutes());
        quiz.setMaxAttempts(request.maxAttempts());

    // Обработка null с Optional
        quiz.setIsPublished(Optional.ofNullable(request.isPublished()).orElse(false));
        quiz.setPassingScore(request.passingScore());
        quiz.setTags(Optional.ofNullable(request.tags()).orElse(new ArrayList<>()));

        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());
        return quiz;
    }

  /**
   * Преобразует сущность Quiz в QuizResponse.
   *
   * @param quiz сущность теста
   * @return DTO с данными теста
   */
    public QuizResponse toResponse(Quiz quiz) {
        return new QuizResponse(
        quiz.getId(),
        quiz.getTitle(),
        quiz.getDescription(),
        quiz.getCategory(),
        quiz.getTimeLimitMinutes(),
        quiz.getMaxAttempts(),
        quiz.getIsPublished(),
        quiz.getPassingScore(),
        quiz.getCreatedAt(),
        quiz.getUpdatedAt(),
        quiz.getTags(),
        0
    );
    }

  /**
   * Обновляет существующую сущность Quiz данными из QuizRequest.
   *
   * @param quiz сущность для обновления
   * @param request DTO с новыми данными
   */
    public void update(Quiz quiz, QuizRequest request) {
        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setCategory(request.category());
        quiz.setTimeLimitMinutes(request.timeLimitMinutes());
        quiz.setMaxAttempts(request.maxAttempts());

    // Обработка null с Optional
        quiz.setIsPublished(Optional.ofNullable(request.isPublished()).orElse(false));
        quiz.setPassingScore(request.passingScore());
        quiz.setTags(Optional.ofNullable(request.tags()).orElse(new ArrayList<>()));

        quiz.setUpdatedAt(LocalDateTime.now());
    }
}