package com.example.onlinetest.service;

import com.example.onlinetest.dto.AnswerRequest;
import com.example.onlinetest.dto.AnswerResponse;
import com.example.onlinetest.mapper.AnswerMapper;
import com.example.onlinetest.model.Answer;
import com.example.onlinetest.model.Question;
import com.example.onlinetest.repository.AnswerRepository;
import com.example.onlinetest.repository.QuestionRepository;
import com.example.onlinetest.service.impl.AnswerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AnswerServiceTest {

  @Mock
  private AnswerRepository answerRepository;

  @Mock
  private QuestionRepository questionRepository;

  @Mock
  private AnswerMapper answerMapper;

  @InjectMocks
  private AnswerServiceImpl answerService;

  private Answer testAnswer;
  private AnswerRequest testRequest;
  private AnswerResponse testResponse;

  @BeforeEach
  void setUp() {
    testAnswer = new Answer();
    testAnswer.setId(1L);
    testAnswer.setText("Answer 1");
    testAnswer.setIsCorrect(true);

    testRequest = new AnswerRequest("Answer 1", true, 1L);
    testResponse = new AnswerResponse(1L, "Answer 1", true);
  }

  // ==================== ПОКРЫТИЕ createAnswer ====================

  @Test
  void createAnswer_Success_ReturnsAnswerResponse() {
    when(questionRepository.findById(1L)).thenReturn(Optional.of(new Question()));
    when(answerMapper.toEntity(testRequest)).thenReturn(testAnswer);
    when(answerRepository.save(any(Answer.class))).thenReturn(testAnswer);
    when(answerMapper.toResponse(testAnswer)).thenReturn(testResponse);

    AnswerResponse result = answerService.createAnswer(testRequest);

    assertNotNull(result);
    verify(answerRepository).save(testAnswer);
  }

  @Test
  void createAnswer_QuestionNotFound_ThrowsException() {
    when(questionRepository.findById(999L)).thenReturn(Optional.empty());
    AnswerRequest requestWithWrongQuestion = new AnswerRequest("Answer", true, 999L);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> answerService.createAnswer(requestWithWrongQuestion));

    assertEquals("Question not found with id: 999", exception.getMessage());
    verify(answerRepository, never()).save(any());
  }

  @Test
  void createAnswer_WithNullQuestionId_Success() {
    AnswerRequest requestWithoutQuestionId = new AnswerRequest("Answer", true, null);
    when(answerMapper.toEntity(requestWithoutQuestionId)).thenReturn(testAnswer);
    when(answerRepository.save(any(Answer.class))).thenReturn(testAnswer);
    when(answerMapper.toResponse(testAnswer)).thenReturn(testResponse);

    AnswerResponse result = answerService.createAnswer(requestWithoutQuestionId);

    assertNotNull(result);
    verify(questionRepository, never()).findById(any());
    verify(answerRepository).save(testAnswer);
  }

  // ==================== ПОКРЫТИЕ updateAnswer ====================

  @Test
  void updateAnswer_Success_ReturnsUpdatedAnswer() {
    when(questionRepository.findById(1L)).thenReturn(Optional.of(new Question()));
    when(answerRepository.findById(1L)).thenReturn(Optional.of(testAnswer));
    when(answerRepository.save(any(Answer.class))).thenReturn(testAnswer);
    when(answerMapper.toResponse(testAnswer)).thenReturn(testResponse);

    AnswerResponse result = answerService.updateAnswer(1L, testRequest);

    assertNotNull(result);
    verify(answerRepository).save(testAnswer);
  }

  @Test
  void updateAnswer_AnswerNotFound_ThrowsException() {
    when(answerRepository.findById(999L)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> answerService.updateAnswer(999L, testRequest));

    assertEquals("Answer not found with id: 999", exception.getMessage());
    verify(answerRepository, never()).save(any());
  }

  @Test
  void updateAnswer_QuestionNotFound_ThrowsException() {
    when(answerRepository.findById(1L)).thenReturn(Optional.of(testAnswer));
    when(questionRepository.findById(999L)).thenReturn(Optional.empty());
    AnswerRequest requestWithWrongQuestion = new AnswerRequest("Answer", true, 999L);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> answerService.updateAnswer(1L, requestWithWrongQuestion));

    assertEquals("Question not found with id: 999", exception.getMessage());
    verify(answerRepository, never()).save(any());
  }

  @Test
  void updateAnswer_WithNullQuestionId_Success() {
    AnswerRequest requestWithoutQuestionId = new AnswerRequest("Updated Answer", true, null);
    when(answerRepository.findById(1L)).thenReturn(Optional.of(testAnswer));
    when(answerRepository.save(any(Answer.class))).thenReturn(testAnswer);
    when(answerMapper.toResponse(testAnswer)).thenReturn(testResponse);

    AnswerResponse result = answerService.updateAnswer(1L, requestWithoutQuestionId);

    assertNotNull(result);
    verify(questionRepository, never()).findById(any());
    verify(answerRepository).save(testAnswer);
  }

  // ==================== ПОКРЫТИЕ deleteAnswer ====================

  @Test
  void deleteAnswer_Success_DeletesAnswer() {
    when(answerRepository.existsById(1L)).thenReturn(true);
    doNothing().when(answerRepository).deleteById(1L);

    assertDoesNotThrow(() -> answerService.deleteAnswer(1L));
    verify(answerRepository).deleteById(1L);
  }

  @Test
  void deleteAnswer_NotFound_ThrowsException() {
    when(answerRepository.existsById(999L)).thenReturn(false);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> answerService.deleteAnswer(999L));

    assertEquals("Answer not found with id: 999", exception.getMessage());
    verify(answerRepository, never()).deleteById(any());
  }

  // ==================== ДРУГИЕ ТЕСТЫ ====================

  @Test
  void getAnswer_Success_ReturnsAnswerResponse() {
    when(answerRepository.findById(1L)).thenReturn(Optional.of(testAnswer));
    when(answerMapper.toResponse(testAnswer)).thenReturn(testResponse);

    AnswerResponse result = answerService.getAnswer(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
  }

  @Test
  void getAnswer_NotFound_ThrowsException() {
    when(answerRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> answerService.getAnswer(999L));
  }

  @Test
  void getAllAnswers_ReturnsList() {
    when(answerRepository.findAll()).thenReturn(List.of(testAnswer));
    when(answerMapper.toResponse(testAnswer)).thenReturn(testResponse);

    List<AnswerResponse> result = answerService.getAllAnswers();

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  void getAnswersByQuestionId_ReturnsList() {
    when(answerRepository.findByQuestionId(1L)).thenReturn(List.of(testAnswer));
    when(answerMapper.toResponse(testAnswer)).thenReturn(testResponse);

    List<AnswerResponse> result = answerService.getAnswersByQuestionId(1L);

    assertFalse(result.isEmpty());
    verify(answerRepository).findByQuestionId(1L);
  }
}