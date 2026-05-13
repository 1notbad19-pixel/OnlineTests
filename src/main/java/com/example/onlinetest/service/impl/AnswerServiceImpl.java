package com.example.onlinetest.service.impl;

import com.example.onlinetest.dto.AnswerRequest;
import com.example.onlinetest.dto.AnswerResponse;
import com.example.onlinetest.mapper.AnswerMapper;
import com.example.onlinetest.model.Answer;
import com.example.onlinetest.repository.AnswerRepository;
import com.example.onlinetest.repository.QuestionRepository;
import com.example.onlinetest.service.AnswerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService {

    private static final String ANSWER_NOT_FOUND_MSG = "Answer not found with id: ";

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final AnswerMapper answerMapper;

    @Override
    @Transactional
  public AnswerResponse createAnswer(AnswerRequest request) {
        Answer answer = answerMapper.toEntity(request);

        if (request.questionId() != null) {
            questionRepository.findById(request.questionId())
                .orElseThrow(() -> new IllegalArgumentException(
                "Question not found with id: " + request.questionId()));
        }

        Answer savedAnswer = answerRepository.save(answer);
        log.info("Answer created with id: {}", savedAnswer.getId());
        return answerMapper.toResponse(savedAnswer);
    }

    @Override
  @Transactional(readOnly = true)
  public AnswerResponse getAnswer(Long id) {
        return answerRepository.findById(id)
        .map(answerMapper::toResponse)
        .orElseThrow(() -> new IllegalArgumentException(ANSWER_NOT_FOUND_MSG + id));
    }

    @Override
  @Transactional(readOnly = true)
  public List<AnswerResponse> getAllAnswers() {
        return answerRepository.findAll().stream()
        .map(answerMapper::toResponse)
        .toList();
    }

    @Override
  @Transactional(readOnly = true)
  public List<AnswerResponse> getAnswersByQuestionId(Long questionId) {
        return answerRepository.findByQuestionId(questionId).stream()
        .map(answerMapper::toResponse)
        .toList();
    }

    @Override
  @Transactional
  public AnswerResponse updateAnswer(Long id, AnswerRequest request) {
        Answer answer = answerRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(ANSWER_NOT_FOUND_MSG + id));

        answerMapper.update(answer, request);

        if (request.questionId() != null) {
            questionRepository.findById(request.questionId())
                .orElseThrow(() -> new IllegalArgumentException(
                "Question not found with id: " + request.questionId()));
        }

        Answer updatedAnswer = answerRepository.save(answer);
        log.info("Answer updated with id: {}", updatedAnswer.getId());
        return answerMapper.toResponse(updatedAnswer);
    }

    @Override
  @Transactional
  public void deleteAnswer(Long id) {
        if (!answerRepository.existsById(id)) {
            throw new IllegalArgumentException(ANSWER_NOT_FOUND_MSG + id);
        }
        answerRepository.deleteById(id);
        log.info("Answer deleted with id: {}", id);
    }
}