package com.example.onlinetest.service.impl;

import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import com.example.onlinetest.mapper.QuizMapper;
import com.example.onlinetest.model.Quiz;
import com.example.onlinetest.repository.QuizRepository;
import com.example.onlinetest.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {
    private final QuizRepository quizRepository;

    @Override
    public QuizResponse createQuiz(QuizRequest request) {
        return QuizMapper.toResponse(quizRepository.save(QuizMapper.toEntity(request)));
    }

    @Override
    public QuizResponse getQuiz(Long id) {
        return quizRepository.findById(id)
                .map(QuizMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Quiz not found: " + id));
    }

    @Override
    public List<QuizResponse> getAllQuizzes(String category, Boolean published, String tag) {
        List<Quiz> quizzes;
        if (category != null) quizzes = quizRepository.findByCategory(category);
        else if (published != null) quizzes = quizRepository.findByPublishedStatus(published);
        else if (tag != null) quizzes = quizRepository.findByTag(tag);
        else quizzes = quizRepository.findAll();

        return quizzes.stream().map(QuizMapper::toResponse).toList();
    }

    @Override
    public QuizResponse updateQuiz(Long id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Quiz not found: " + id));
        QuizMapper.update(quiz, request);
        return QuizMapper.toResponse(quizRepository.save(quiz));
    }

    @Override
    public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id))
            throw new RuntimeException("Quiz not found: " + id);
        quizRepository.deleteById(id);
    }
}