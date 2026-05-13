package com.example.onlinetest.service.impl;

import com.example.onlinetest.dto.QuestionRequest;
import com.example.onlinetest.dto.QuestionResponse;
import com.example.onlinetest.mapper.QuestionMapper;
import com.example.onlinetest.model.Question;
import com.example.onlinetest.repository.QuestionRepository;
import com.example.onlinetest.repository.QuizRepository;
import com.example.onlinetest.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private static final String QUESTION_NOT_FOUND_MSG = "Question not found with id: ";

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;
    private final QuestionMapper questionMapper;

    @Override
  @Transactional
  public QuestionResponse createQuestion(QuestionRequest request) {
        Question question = questionMapper.toEntity(request);

        if (request.quizId() != null) {
            quizRepository.findById(request.quizId())
                .orElseThrow(() -> new IllegalArgumentException(
                "Quiz not found with id: " + request.quizId()));
        }

        Question savedQuestion = questionRepository.save(question);
        log.info("Question created with id: {}", savedQuestion.getId());
        return questionMapper.toResponse(savedQuestion);
    }

    @Override
  @Transactional(readOnly = true)
  public QuestionResponse getQuestion(Long id) {
        return questionRepository.findById(id)
        .map(questionMapper::toResponse)
        .orElseThrow(() -> new IllegalArgumentException(QUESTION_NOT_FOUND_MSG + id));
    }

    @Override
  @Transactional(readOnly = true)
  public List<QuestionResponse> getAllQuestions() {
        return questionRepository.findAll().stream()
        .map(questionMapper::toResponse)
        .toList();
    }

    @Override
  @Transactional(readOnly = true)
  public List<QuestionResponse> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId).stream()
        .map(questionMapper::toResponse)
        .toList();
    }

    @Override
  @Transactional
  public QuestionResponse updateQuestion(Long id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(QUESTION_NOT_FOUND_MSG + id));

        questionMapper.update(question, request);

        if (request.quizId() != null) {
            quizRepository.findById(request.quizId())
                .orElseThrow(() -> new IllegalArgumentException(
                "Quiz not found with id: " + request.quizId()));
        }

        Question updatedQuestion = questionRepository.save(question);
        log.info("Question updated with id: {}", updatedQuestion.getId());
        return questionMapper.toResponse(updatedQuestion);
    }

    @Override
  @Transactional
  public void deleteQuestion(Long id) {
        if (!questionRepository.existsById(id)) {
            throw new IllegalArgumentException(QUESTION_NOT_FOUND_MSG + id);
        }
        questionRepository.deleteById(id);
        log.info("Question deleted with id: {}", id);
    }
}