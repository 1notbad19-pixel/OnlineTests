package com.example.onlinetest.service;

import com.example.onlinetest.dto.AnswerRequest;
import com.example.onlinetest.dto.QuestionRequest;
import com.example.onlinetest.dto.QuestionResponse;
import com.example.onlinetest.mapper.QuestionMapper;
import com.example.onlinetest.model.Question;
import com.example.onlinetest.model.Quiz;
import com.example.onlinetest.repository.QuestionRepository;
import com.example.onlinetest.repository.QuizRepository;
import com.example.onlinetest.service.impl.QuestionServiceImpl;
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
class QuestionServiceTest {

  @Mock
  private QuestionRepository questionRepository;

  @Mock
  private QuizRepository quizRepository;

  @Mock
  private QuestionMapper questionMapper;

  @InjectMocks
  private QuestionServiceImpl questionService;

  private Question testQuestion;
  private QuestionRequest testRequest;
  private QuestionResponse testResponse;

  @BeforeEach
  void setUp() {
    testQuestion = new Question();
    testQuestion.setId(1L);
    testQuestion.setText("Question 1");
    testQuestion.setType("SINGLE");
    testQuestion.setPoints(10);

    testRequest = new QuestionRequest("Question 1", "SINGLE", 10, List.of(), 1L);
    testResponse = new QuestionResponse(1L, "Question 1", "SINGLE", 10, List.of());
  }

  // ==================== ПОКРЫТИЕ createQuestion ====================

  @Test
  void createQuestion_Success_ReturnsQuestionResponse() {
    when(quizRepository.findById(1L)).thenReturn(Optional.of(new Quiz()));
    when(questionMapper.toEntity(testRequest)).thenReturn(testQuestion);
    when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);
    when(questionMapper.toResponse(testQuestion)).thenReturn(testResponse);

    QuestionResponse result = questionService.createQuestion(testRequest);

    assertNotNull(result);
    verify(questionRepository).save(testQuestion);
  }

  @Test
  void createQuestion_QuizNotFound_ThrowsException() {
    when(quizRepository.findById(999L)).thenReturn(Optional.empty());
    QuestionRequest requestWithWrongQuiz = new QuestionRequest("Question", "SINGLE", 10, List.of(), 999L);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> questionService.createQuestion(requestWithWrongQuiz));

    assertEquals("Quiz not found with id: 999", exception.getMessage());
    verify(questionRepository, never()).save(any());
  }

  @Test
  void createQuestion_WithNullQuizId_Success() {
    QuestionRequest requestWithoutQuizId = new QuestionRequest("Question", "SINGLE", 10, List.of(), null);
    when(questionMapper.toEntity(requestWithoutQuizId)).thenReturn(testQuestion);
    when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);
    when(questionMapper.toResponse(testQuestion)).thenReturn(testResponse);

    QuestionResponse result = questionService.createQuestion(requestWithoutQuizId);

    assertNotNull(result);
    verify(quizRepository, never()).findById(any());
    verify(questionRepository).save(testQuestion);
  }

  // ==================== ПОКРЫТИЕ updateQuestion ====================

  @Test
  void updateQuestion_Success_ReturnsUpdatedQuestion() {
    when(quizRepository.findById(1L)).thenReturn(Optional.of(new Quiz()));
    when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
    when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);
    when(questionMapper.toResponse(testQuestion)).thenReturn(testResponse);

    QuestionResponse result = questionService.updateQuestion(1L, testRequest);

    assertNotNull(result);
    verify(questionRepository).save(testQuestion);
  }

  @Test
  void updateQuestion_QuestionNotFound_ThrowsException() {
    when(questionRepository.findById(999L)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> questionService.updateQuestion(999L, testRequest));

    assertEquals("Question not found with id: 999", exception.getMessage());
    verify(questionRepository, never()).save(any());
  }

  @Test
  void updateQuestion_QuizNotFound_ThrowsException() {
    when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
    when(quizRepository.findById(999L)).thenReturn(Optional.empty());
    QuestionRequest requestWithWrongQuiz = new QuestionRequest("Question", "SINGLE", 10, List.of(), 999L);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> questionService.updateQuestion(1L, requestWithWrongQuiz));

    assertEquals("Quiz not found with id: 999", exception.getMessage());
    verify(questionRepository, never()).save(any());
  }

  @Test
  void updateQuestion_WithNullQuizId_Success() {
    QuestionRequest requestWithoutQuizId = new QuestionRequest("Updated Question", "SINGLE", 10, List.of(), null);
    when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
    when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);
    when(questionMapper.toResponse(testQuestion)).thenReturn(testResponse);

    QuestionResponse result = questionService.updateQuestion(1L, requestWithoutQuizId);

    assertNotNull(result);
    verify(quizRepository, never()).findById(any());
    verify(questionRepository).save(testQuestion);
  }

  // ==================== ПОКРЫТИЕ deleteQuestion ====================

  @Test
  void deleteQuestion_Success_DeletesQuestion() {
    when(questionRepository.existsById(1L)).thenReturn(true);
    doNothing().when(questionRepository).deleteById(1L);

    assertDoesNotThrow(() -> questionService.deleteQuestion(1L));
    verify(questionRepository).deleteById(1L);
  }

  @Test
  void deleteQuestion_NotFound_ThrowsException() {
    when(questionRepository.existsById(999L)).thenReturn(false);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> questionService.deleteQuestion(999L));

    assertEquals("Question not found with id: 999", exception.getMessage());
    verify(questionRepository, never()).deleteById(any());
  }

  // ==================== ДРУГИЕ ТЕСТЫ ====================

  @Test
  void getQuestion_Success_ReturnsQuestionResponse() {
    when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
    when(questionMapper.toResponse(testQuestion)).thenReturn(testResponse);

    QuestionResponse result = questionService.getQuestion(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
  }

  @Test
  void getQuestion_NotFound_ThrowsException() {
    when(questionRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> questionService.getQuestion(999L));
  }

  @Test
  void getAllQuestions_ReturnsList() {
    when(questionRepository.findAll()).thenReturn(List.of(testQuestion));
    when(questionMapper.toResponse(testQuestion)).thenReturn(testResponse);

    List<QuestionResponse> result = questionService.getAllQuestions();

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }

  @Test
  void getQuestionsByQuizId_ReturnsList() {
    when(questionRepository.findByQuizId(1L)).thenReturn(List.of(testQuestion));
    when(questionMapper.toResponse(testQuestion)).thenReturn(testResponse);

    List<QuestionResponse> result = questionService.getQuestionsByQuizId(1L);

    assertFalse(result.isEmpty());
    verify(questionRepository).findByQuizId(1L);
  }
}