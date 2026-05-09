package com.example.onlinetest.service;

import com.example.onlinetest.dto.FullQuizRequest;
import com.example.onlinetest.dto.QuestionRequest;
import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import com.example.onlinetest.exception.QuizServiceException;
import com.example.onlinetest.mapper.QuestionMapper;
import com.example.onlinetest.mapper.QuizMapper;
import com.example.onlinetest.model.Question;
import com.example.onlinetest.model.Quiz;
import com.example.onlinetest.model.Tag;
import com.example.onlinetest.model.User;
import com.example.onlinetest.repository.QuizRepository;
import com.example.onlinetest.repository.TagRepository;
import com.example.onlinetest.repository.UserRepository;
import com.example.onlinetest.service.impl.QuizServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QuizServiceTest {

  @Mock
  private QuizRepository quizRepository;

  @Mock
  private TagRepository tagRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private QuizMapper quizMapper;

  @Mock
  private QuestionMapper questionMapper;

  @Mock
  private QuizCacheService cacheService;

  @InjectMocks
  private QuizServiceImpl quizService;

  private User testUser;
  private Quiz testQuiz;
  private QuizRequest testRequest;
  private QuizResponse testResponse;

  @BeforeEach
  void setUp() {
    testUser = new User();
    testUser.setId(1L);
    testUser.setUsername("admin");

    testQuiz = new Quiz();
    testQuiz.setId(1L);
    testQuiz.setTitle("Test Quiz");
    testQuiz.setCategory("Programming");
    testQuiz.setQuestions(new HashSet<>());

    testRequest = new QuizRequest(
        "Test Quiz", "Description", "Programming",
        30, 3, true, 70, List.of("test")
    );

    testResponse = new QuizResponse(
        1L, "Test Quiz", "Description", "Programming",
        30, 3, true, 70, LocalDateTime.now(), LocalDateTime.now(),
        List.of("test"), 0
    );
  }

  @Test
  void createQuiz_Success_ReturnsQuizResponse() {
    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(testRequest)).thenReturn(testQuiz);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.createQuiz(testRequest);

    assertNotNull(result);
    assertEquals(testResponse.title(), result.title());
    verify(quizRepository).save(testQuiz);
    verify(cacheService).invalidate();
  }

  @Test
  void createQuiz_WithTags_ReturnsQuizResponse() {
    QuizRequest requestWithTags = new QuizRequest(
        "Tag Quiz", "Description", "Programming",
        30, 3, true, 70, List.of("java", "spring")
    );

    Tag tag1 = new Tag();
    tag1.setName("java");
    Tag tag2 = new Tag();
    tag2.setName("spring");

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(requestWithTags)).thenReturn(testQuiz);
    when(tagRepository.findByName("java")).thenReturn(Optional.empty());
    when(tagRepository.findByName("spring")).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(tag1, tag2);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.createQuiz(requestWithTags);

    assertNotNull(result);
    verify(cacheService).invalidate();
  }

  @Test
  void getQuiz_Success_ReturnsQuizResponse() {
    when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.getQuiz(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
    verify(quizRepository).findById(1L);
  }

  @Test
  void getQuiz_NotFound_ThrowsException() {
    when(quizRepository.findById(999L)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> quizService.getQuiz(999L));

    assertEquals("Quiz not found with id: 999", exception.getMessage());
  }

  // ==================== GET QUIZ WITH DETAILS ====================

  @Test
  void getQuizWithDetails_Success_ReturnsQuizResponse() {
    when(quizRepository.findByIdWithAllDetails(1L)).thenReturn(Optional.of(testQuiz));
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.getQuizWithDetails(1L);

    assertNotNull(result);
    verify(quizRepository).findByIdWithAllDetails(1L);
  }

  @Test
  void updateQuiz_Success_ReturnsUpdatedQuiz() {
    when(quizRepository.findById(1L)).thenReturn(Optional.of(testQuiz));
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.updateQuiz(1L, testRequest);

    assertNotNull(result);
    verify(quizRepository).save(testQuiz);
    verify(cacheService).invalidate();
  }

  @Test
  void updateQuiz_NotFound_ThrowsException() {
    when(quizRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class,
        () -> quizService.updateQuiz(999L, testRequest));
  }

  @Test
  void deleteQuiz_Success_DeletesQuiz() {
    when(quizRepository.existsById(1L)).thenReturn(true);
    doNothing().when(quizRepository).deleteById(1L);

    assertDoesNotThrow(() -> quizService.deleteQuiz(1L));
    verify(quizRepository).deleteById(1L);
    verify(cacheService).invalidate();
  }

  @Test
  void deleteQuiz_NotFound_ThrowsException() {
    when(quizRepository.existsById(999L)).thenReturn(false);

    assertThrows(IllegalArgumentException.class,
        () -> quizService.deleteQuiz(999L));
  }

  @Test
  void getAllQuizzes_WithCategory_ReturnsFilteredList() {
    when(quizRepository.findByCategoryIgnoreCase("Programming")).thenReturn(List.of(testQuiz));
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    List<QuizResponse> result = quizService.getAllQuizzes("Programming", null, null);

    assertFalse(result.isEmpty());
    verify(quizRepository).findByCategoryIgnoreCase("Programming");
  }

  @Test
  void getAllQuizzes_WithPublished_ReturnsFilteredList() {
    when(quizRepository.findByIsPublished(true)).thenReturn(List.of(testQuiz));
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    List<QuizResponse> result = quizService.getAllQuizzes(null, true, null);

    assertFalse(result.isEmpty());
    verify(quizRepository).findByIsPublished(true);
  }

  @Test
  void getAllQuizzes_WithTag_ReturnsFilteredList() {
    when(quizRepository.findByTag("test")).thenReturn(List.of(testQuiz));
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    List<QuizResponse> result = quizService.getAllQuizzes(null, null, "test");

    assertFalse(result.isEmpty());
    verify(quizRepository).findByTag("test");
  }

  @Test
  void getAllQuizzes_NoFilters_ReturnsAll() {
    when(quizRepository.findAll()).thenReturn(List.of(testQuiz));
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    List<QuizResponse> result = quizService.getAllQuizzes(null, null, null);

    assertFalse(result.isEmpty());
    verify(quizRepository).findAll();
  }

  @Test
  void createFullQuiz_Success_ReturnsQuizResponse() {
    QuestionRequest questionRequest = new QuestionRequest("What is Java?", "SINGLE", 10, List.of());
    FullQuizRequest fullRequest = new FullQuizRequest(testRequest, List.of(questionRequest));

    Question mockQuestion = new Question();
    mockQuestion.setId(1L);

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(testRequest)).thenReturn(testQuiz);
    when(questionMapper.toEntity(any(QuestionRequest.class))).thenReturn(mockQuestion);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.createFullQuiz(fullRequest);

    assertNotNull(result);
    verify(cacheService).invalidate();
  }

  @Test
  void createFullQuiz_TooManyQuestions_ThrowsException() {
    List<QuestionRequest> questions = new ArrayList<>();
    for (int i = 0; i < 11; i++) {
      questions.add(new QuestionRequest("Q" + i, "SINGLE", 10, List.of()));
    }
    FullQuizRequest fullRequest = new FullQuizRequest(testRequest, questions);

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(testRequest)).thenReturn(testQuiz);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(questionMapper.toEntity(any(QuestionRequest.class))).thenReturn(new Question());

    assertThrows(QuizServiceException.class,
        () -> quizService.createFullQuiz(fullRequest));
  }

  @Test
  void createFullQuizWithoutTransaction_Success_ReturnsQuizResponse() {
    QuestionRequest questionRequest = new QuestionRequest("What is Java?", "SINGLE", 10, List.of());
    FullQuizRequest fullRequest = new FullQuizRequest(testRequest, List.of(questionRequest));

    Question mockQuestion = new Question();
    mockQuestion.setId(1L);

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(testRequest)).thenReturn(testQuiz);
    when(questionMapper.toEntity(any(QuestionRequest.class))).thenReturn(mockQuestion);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.createFullQuizWithoutTransaction(fullRequest);

    assertNotNull(result);
    verify(cacheService).invalidate();
  }

  @Test
  void createFullQuizWithoutTransaction_TooManyQuestions_ThrowsException() {
    List<QuestionRequest> questions = new ArrayList<>();
    for (int i = 0; i < 11; i++) {
      questions.add(new QuestionRequest("Q" + i, "SINGLE", 10, List.of()));
    }
    FullQuizRequest fullRequest = new FullQuizRequest(testRequest, questions);

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(testRequest)).thenReturn(testQuiz);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(questionMapper.toEntity(any(QuestionRequest.class))).thenReturn(new Question());

    assertThrows(QuizServiceException.class,
        () -> quizService.createFullQuizWithoutTransaction(fullRequest));
  }

  @Test
  void getQuizzesWithFilters_Success_ReturnsPage() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Quiz> quizPage = new PageImpl<>(List.of(testQuiz));

    when(quizRepository.findQuizzesWithFilters(any(), any(), any(), any(Pageable.class)))
        .thenReturn(quizPage);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    Page<QuizResponse> result = quizService.getQuizzesWithFilters("Programming", true, 1, pageable);

    assertNotNull(result);
    assertEquals(1, result.getTotalElements());
  }

  @Test
  void getQuizzesWithFiltersNative_Success_ReturnsPage() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<Quiz> quizPage = new PageImpl<>(List.of(testQuiz));

    when(quizRepository.findQuizzesWithFiltersNative(any(), any(), any(), any(Pageable.class)))
        .thenReturn(quizPage);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    Page<QuizResponse> result = quizService.getQuizzesWithFiltersNative("Programming", true, 1, pageable);

    assertNotNull(result);
  }

  @Test
  void deleteByCategory_WithQuizzes_ReturnsCount() {
    when(quizRepository.findByCategoryIgnoreCase("Programming")).thenReturn(List.of(testQuiz));
    doNothing().when(quizRepository).deleteAll(any());

    int result = quizService.deleteByCategory("Programming");

    assertEquals(1, result);
    verify(quizRepository).deleteAll(any());
  }

  @Test
  void deleteByCategory_NoQuizzes_ReturnsZero() {
    when(quizRepository.findByCategoryIgnoreCase("Empty")).thenReturn(List.of());

    int result = quizService.deleteByCategory("Empty");

    assertEquals(0, result);
    verify(quizRepository, never()).deleteAll(any());
  }

  @Test
  void deleteByPublishedStatus_WithQuizzes_ReturnsCount() {
    when(quizRepository.findByIsPublished(true)).thenReturn(List.of(testQuiz));
    doNothing().when(quizRepository).deleteAll(any());

    int result = quizService.deleteByPublishedStatus(true);

    assertEquals(1, result);
    verify(quizRepository).deleteAll(any());
  }

  @Test
  void deleteByPublishedStatus_NoQuizzes_ReturnsZero() {
    when(quizRepository.findByIsPublished(false)).thenReturn(List.of());

    int result = quizService.deleteByPublishedStatus(false);

    assertEquals(0, result);
    verify(quizRepository, never()).deleteAll(any());
  }

  @Test
  void deleteByTag_WithQuizzes_ReturnsCount() {
    Quiz quiz1 = new Quiz();
    quiz1.setId(1L);
    Quiz quiz2 = new Quiz();
    quiz2.setId(2L);
    List<Quiz> quizzes = List.of(quiz1, quiz2);

    when(quizRepository.findByTag("java")).thenReturn(quizzes);
    doNothing().when(quizRepository).deleteAll(quizzes);

    int result = quizService.deleteByTag("java");

    assertEquals(2, result);
    verify(quizRepository).deleteAll(quizzes);
  }

  @Test
  void deleteByTag_NoQuizzes_ReturnsZero() {
    when(quizRepository.findByTag("nonexistent")).thenReturn(List.of());

    int result = quizService.deleteByTag("nonexistent");

    assertEquals(0, result);
    verify(quizRepository, never()).deleteAll(any());
  }

  @Test
  void deleteByMinQuestions_WithQuizzesToDelete_ReturnsCount() {
    Quiz quiz1 = new Quiz();
    quiz1.setId(1L);
    quiz1.setQuestions(new HashSet<>());

    Quiz quiz2 = new Quiz();
    quiz2.setId(2L);
    quiz2.setQuestions(new HashSet<>());

    Quiz quiz3 = new Quiz();
    quiz3.setId(3L);
    Set<Question> questions = new HashSet<>();
    questions.add(new Question());
    quiz3.setQuestions(questions);

    List<Quiz> allQuizzes = List.of(quiz1, quiz2, quiz3);

    when(quizRepository.findAll()).thenReturn(allQuizzes);
    doNothing().when(quizRepository).deleteAll(any());

    int result = quizService.deleteByMinQuestions(1);

    assertEquals(2, result);
    verify(quizRepository).deleteAll(any());
  }

  @Test
  void deleteByMinQuestions_NoQuizzesToDelete_ReturnsZero() {
    Quiz quiz1 = new Quiz();
    Set<Question> questions = new HashSet<>();
    questions.add(new Question());
    quiz1.setQuestions(questions);

    when(quizRepository.findAll()).thenReturn(List.of(quiz1));

    int result = quizService.deleteByMinQuestions(1);

    assertEquals(0, result);
    verify(quizRepository, never()).deleteAll(any());
  }

  @Test
  void deleteAllQuizzes_Success_ReturnsCount() {
    when(quizRepository.count()).thenReturn(5L);
    doNothing().when(quizRepository).deleteAll();

    long result = quizService.deleteAllQuizzes();

    assertEquals(5L, result);
    verify(quizRepository).deleteAll();
  }

  @Test
  void invalidateCache_Success() {
    quizService.invalidateCache();
    verify(cacheService).invalidate();
  }

  @Test
  void createQuiz_WhenUserNotFound_CreatesDefaultUser() {
    when(userRepository.findByUsername("admin")).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
      User user = invocation.getArgument(0);
      user.setId(1L);
      return user;
    });
    when(quizMapper.toEntity(testRequest)).thenReturn(testQuiz);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.createQuiz(testRequest);

    assertNotNull(result);
    verify(userRepository).save(any(User.class));
    verify(cacheService).invalidate();
  }

  @Test
  void createQuiz_WithTags_WhenTagsNotEmpty() {
    QuizRequest requestWithTags = new QuizRequest(
        "Tag Quiz", "Description", "Programming",
        30, 3, true, 70, List.of("java", "spring")
    );

    Tag existingTag = new Tag();
    existingTag.setName("java");

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(requestWithTags)).thenReturn(testQuiz);
    when(tagRepository.findByName("java")).thenReturn(Optional.of(existingTag));
    when(tagRepository.findByName("spring")).thenReturn(Optional.empty());
    when(tagRepository.save(any(Tag.class))).thenReturn(new Tag());
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.createQuiz(requestWithTags);

    assertNotNull(result);
    verify(cacheService).invalidate();
  }

  @Test
  void getQuizzesWithFilters_CacheHit_ReturnsCachedResult() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<QuizResponse> cachedPage = new PageImpl<>(List.of(testResponse));

    when(cacheService.get(any())).thenReturn(cachedPage);

    Page<QuizResponse> result = quizService.getQuizzesWithFilters(
        "Programming", true, 1, pageable);

    assertNotNull(result);
    verify(quizRepository, never()).findQuizzesWithFilters(any(), any(), any(), any());
  }

  @Test
  void getQuizzesWithFiltersNative_CacheHit_ReturnsCachedResult() {
    Pageable pageable = PageRequest.of(0, 10);
    Page<QuizResponse> cachedPage = new PageImpl<>(List.of(testResponse));

    when(cacheService.get(any())).thenReturn(cachedPage);

    Page<QuizResponse> result = quizService.getQuizzesWithFiltersNative(
        "Programming", true, 1, pageable);

    assertNotNull(result);
    verify(quizRepository, never()).findQuizzesWithFiltersNative(any(), any(), any(), any());
  }

  @Test
  void createFullQuiz_WithQuestions_WhenQuestionsNotEmpty() {
    QuestionRequest questionRequest = new QuestionRequest("What is Java?", "SINGLE", 10, List.of());
    FullQuizRequest fullRequest = new FullQuizRequest(testRequest, List.of(questionRequest));

    Question mockQuestion = new Question();
    mockQuestion.setId(1L);

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(testRequest)).thenReturn(testQuiz);
    when(questionMapper.toEntity(any(QuestionRequest.class))).thenReturn(mockQuestion);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.createFullQuiz(fullRequest);

    assertNotNull(result);
    verify(cacheService).invalidate();
  }

  @Test
  void createFullQuizWithoutTransaction_WithQuestions_WhenQuestionsNotEmpty() {
    QuestionRequest questionRequest = new QuestionRequest("What is Java?", "SINGLE", 10, List.of());
    FullQuizRequest fullRequest = new FullQuizRequest(testRequest, List.of(questionRequest));

    Question mockQuestion = new Question();
    mockQuestion.setId(1L);

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(testRequest)).thenReturn(testQuiz);
    when(questionMapper.toEntity(any(QuestionRequest.class))).thenReturn(mockQuestion);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    QuizResponse result = quizService.createFullQuizWithoutTransaction(fullRequest);

    assertNotNull(result);
    verify(cacheService).invalidate();
  }

  @Test
  void createQuizzesBulk_Success_ReturnsListOfResponses() {
    List<QuizRequest> requests = List.of(testRequest, testRequest);

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(any(QuizRequest.class))).thenReturn(testQuiz);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    List<QuizResponse> result = quizService.createQuizzesBulk(requests);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(quizRepository, times(2)).save(any(Quiz.class));
    verify(cacheService, times(2)).invalidate();
  }

  @Test
  void createQuizzesBulkWithoutTransaction_Success_FirstTwoSavedThirdThrowsException() {
    List<QuizRequest> requests = List.of(testRequest, testRequest, testRequest);

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(any(QuizRequest.class))).thenReturn(testQuiz);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    assertThrows(QuizServiceException.class, () ->
        quizService.createQuizzesBulkWithoutTransaction(requests));

    verify(quizRepository, times(2)).save(any(Quiz.class));
    verify(cacheService, times(2)).invalidate();
  }

  @Test
  void createQuizzesBulkWithoutTransaction_WithTwoRequests_AllSaved() {
    List<QuizRequest> requests = List.of(testRequest, testRequest);

    when(userRepository.findByUsername("admin")).thenReturn(Optional.of(testUser));
    when(quizMapper.toEntity(any(QuizRequest.class))).thenReturn(testQuiz);
    when(quizRepository.save(any(Quiz.class))).thenReturn(testQuiz);
    when(quizMapper.toResponse(testQuiz)).thenReturn(testResponse);

    List<QuizResponse> result = quizService.createQuizzesBulkWithoutTransaction(requests);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(quizRepository, times(2)).save(any(Quiz.class));
  }
}