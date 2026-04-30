package com.example.onlinetest.mapper;

import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import com.example.onlinetest.model.Quiz;
import com.example.onlinetest.model.Tag;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class QuizMapper {

    public Quiz toEntity(QuizRequest request) {
        Quiz quiz = new Quiz();
        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setCategory(request.category());
        quiz.setTimeLimitMinutes(request.timeLimitMinutes());
        quiz.setMaxAttempts(request.maxAttempts());
        quiz.setIsPublished(Optional.ofNullable(request.isPublished()).orElse(false));
        quiz.setPassingScore(request.passingScore());
        quiz.setCreatedAt(LocalDateTime.now());
        quiz.setUpdatedAt(LocalDateTime.now());

        if (request.tags() != null && !request.tags().isEmpty()) {
            Set<Tag> tags = request.tags().stream()
                .map(tagName -> {
                    Tag tag = new Tag();
                    tag.setName(tagName);
                    return tag;
                })
                .collect(Collectors.toSet());
            quiz.setTags(tags);
        } else {
            quiz.setTags(new HashSet<>());
        }
        return quiz;
    }

    public QuizResponse toResponse(Quiz quiz) {
        Set<String> tagNames = quiz.getTags() != null
            ? quiz.getTags().stream().map(Tag::getName).collect(Collectors.toSet())
            : new HashSet<>();

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
        tagNames.stream().toList(),
        quiz.getQuestions() != null ? quiz.getQuestions().size() : 0
    );
    }

    public void update(Quiz quiz, QuizRequest request) {
        quiz.setTitle(request.title());
        quiz.setDescription(request.description());
        quiz.setCategory(request.category());
        quiz.setTimeLimitMinutes(request.timeLimitMinutes());
        quiz.setMaxAttempts(request.maxAttempts());
        quiz.setIsPublished(Optional.ofNullable(request.isPublished()).orElse(false));
        quiz.setPassingScore(request.passingScore());
        quiz.setUpdatedAt(LocalDateTime.now());

        if (request.tags() != null) {
            Set<Tag> tags = request.tags().stream()
                .map(tagName -> {
                    Tag tag = new Tag();
                    tag.setName(tagName);
                    return tag;
                })
                .collect(Collectors.toSet());
            quiz.getTags().clear();
            quiz.getTags().addAll(tags);
        }
    }
}