package com.example.onlinetest.service.impl;

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
import com.example.onlinetest.service.QuizCacheService;
import com.example.onlinetest.service.QuizService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

  private static final String QUIZ_NOT_FOUND_MSG = "Quiz not found with id: ";
  private static final String DEFAULT_USERNAME = "admin";
  private static final String DEFAULT_EMAIL = "admin@example.com";
  private static final String DEFAULT_PASSWORD = "password";

  private final QuizRepository quizRepository;
  private final TagRepository tagRepository;
  private final UserRepository userRepository;
  private final QuizMapper quizMapper;
  private final QuestionMapper questionMapper;
  private final QuizCacheService cacheService;

  private User getDefaultUser() {
    return userRepository.findByUsername(DEFAULT_USERNAME)
        .orElseGet(() -> {
          User newUser = new User();
          newUser.setUsername(DEFAULT_USERNAME);
          newUser.setEmail(DEFAULT_EMAIL);
          newUser.setPassword(DEFAULT_PASSWORD);
          newUser.setCreatedAt(LocalDateTime.now());
          return userRepository.save(newUser);
        });
  }

  @Override
  @Transactional
  public QuizResponse createQuiz(QuizRequest request, Long userId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

    Quiz quiz = quizMapper.toEntity(request);
    quiz.setCreatedBy(user);

    if (request.tags() != null && !request.tags().isEmpty()) {
      List<Tag> processedTags = request.tags().stream()
          .map(tagName -> tagRepository.findByName(tagName)
              .orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setName(tagName);
                return tagRepository.save(newTag);
              }))
          .toList();
      quiz.setTags(new HashSet<>(processedTags));
    }

    Quiz savedQuiz = quizRepository.save(quiz);
    return quizMapper.toResponse(savedQuiz);
  }

  @Override
  @Transactional
  public QuizResponse createFullQuiz(FullQuizRequest request) {
    Quiz quiz = quizMapper.toEntity(request.quiz());
    quiz.setCreatedBy(getDefaultUser());
    Quiz savedQuiz = quizRepository.save(quiz);

    if (request.questions() != null && !request.questions().isEmpty()) {
      for (QuestionRequest qReq : request.questions()) {
        Question question = questionMapper.toEntity(qReq);
        question.setQuiz(savedQuiz);
        savedQuiz.getQuestions().add(question);
      }

      if (request.questions().size() > 10) {
        throw new QuizServiceException("ROLLBACK: Too many questions! Nothing will be saved.");
      }

      cacheService.invalidate();
      savedQuiz = quizRepository.save(savedQuiz);
    }

    return quizMapper.toResponse(savedQuiz);
  }

  @Override
  public QuizResponse createFullQuizWithoutTransaction(FullQuizRequest request) {
    Quiz quiz = quizMapper.toEntity(request.quiz());
    quiz.setCreatedBy(getDefaultUser());
    Quiz savedQuiz = quizRepository.save(quiz);

    if (request.questions() != null && !request.questions().isEmpty()) {
      for (QuestionRequest qReq : request.questions()) {
        Question question = questionMapper.toEntity(qReq);
        question.setQuiz(savedQuiz);
        savedQuiz.getQuestions().add(question);
      }

      if (request.questions().size() > 10) {
        throw new QuizServiceException("Too many questions! Quiz saved but questions were not.");
      }
    }

    cacheService.invalidate();
    return quizMapper.toResponse(savedQuiz);
  }

  @Override
  @Transactional(readOnly = true)
  public QuizResponse getQuiz(Long id) {
    Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException(
            QUIZ_NOT_FOUND_MSG + id));
    return quizMapper.toResponse(quiz);
  }

  @Override
  @Transactional(readOnly = true)
  public QuizResponse getQuizWithDetails(Long id) {
    Quiz quiz = quizRepository.findByIdWithAllDetails(id)
        .orElseThrow(() -> new IllegalArgumentException(
            QUIZ_NOT_FOUND_MSG + id));
    return quizMapper.toResponse(quiz);
  }

  @Override
  @Transactional(readOnly = true)
  public List<QuizResponse> getAllQuizzes(String category, Boolean published,
      String tag) {
    List<Quiz> quizzes;

    if (category != null) {
      quizzes = quizRepository.findByCategoryIgnoreCase(category);
    } else if (published != null) {
      quizzes = quizRepository.findByIsPublished(published);
    } else if (tag != null) {
      quizzes = quizRepository.findByTag(tag);
    } else {
      quizzes = quizRepository.findAll();
    }

    return quizzes.stream()
        .map(quizMapper::toResponse)
        .toList();
  }

  @Override
  @Transactional(readOnly = true)
  public Page<QuizResponse> getQuizzesWithFilters(String category,
      Boolean published, Integer minQuestions, Pageable pageable) {
    QuizCacheService.CacheKey key = new QuizCacheService.CacheKey(
        category,
        published,
        minQuestions,
        pageable.getPageNumber(),
        pageable.getPageSize()
    );

    Page<QuizResponse> cached = cacheService.get(key);
    if (cached != null) {
      log.info("✅ CACHE HIT! Данные из кэша для ключа: {}", key);
      return cached;
    }

    log.info("❌ CACHE MISS! Идём в базу данных для ключа: {}", key);
    Page<Quiz> quizPage = quizRepository.findQuizzesWithFilters(
        category, published, minQuestions, pageable);
    Page<QuizResponse> responsePage = quizPage.map(quizMapper::toResponse);

    cacheService.put(key, responsePage);
    log.info("💾 Данные сохранены в кэш");

    return responsePage;
  }

  @Override
  @Transactional
  public QuizResponse updateQuiz(Long id, QuizRequest request) {
    log.info("Updating quiz with id: {}", id);

    Quiz quiz = quizRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));

    quiz.setTitle(request.title());
    quiz.setDescription(request.description());
    quiz.setCategory(request.category());
    quiz.setTimeLimitMinutes(request.timeLimitMinutes());
    quiz.setMaxAttempts(request.maxAttempts());
    quiz.setIsPublished(request.isPublished() != null ? request.isPublished() : false);
    quiz.setPassingScore(request.passingScore());
    quiz.setUpdatedAt(LocalDateTime.now());

    if (request.tags() != null) {
      Set<Tag> tags = request.tags().stream()
          .map(tagName -> tagRepository.findByName(tagName)
              .orElseGet(() -> {
                Tag newTag = new Tag();
                newTag.setName(tagName);
                return tagRepository.save(newTag);
              }))
          .collect(Collectors.toSet());
      quiz.setTags(tags);
    }

    Quiz updatedQuiz = quizRepository.save(quiz);
    cacheService.invalidate();
    log.info("Quiz updated successfully with id: {}", updatedQuiz.getId());

    return quizMapper.toResponse(updatedQuiz);
  }

  @Override
  @Transactional(readOnly = true)
  public Page<QuizResponse> getQuizzesWithFiltersNative(String category,
      Boolean published, Integer minQuestions, Pageable pageable) {
    QuizCacheService.CacheKey key = new QuizCacheService.CacheKey(
        category,
        published,
        minQuestions,
        pageable.getPageNumber(),
        pageable.getPageSize()
    );

    Page<QuizResponse> cached = cacheService.get(key);
    if (cached != null) {
      log.info("✅ CACHE HIT! (native) Данные из кэша");
      return cached;
    }

    log.info("❌ CACHE MISS! (native) Идём в базу данных");
    Page<Quiz> quizPage = quizRepository.findQuizzesWithFiltersNative(
        category, published, minQuestions, pageable);
    Page<QuizResponse> responsePage = quizPage.map(quizMapper::toResponse);

    cacheService.put(key, responsePage);

    return responsePage;
  }

  @Override
  @Transactional
  public void deleteQuiz(Long id) {
    if (!quizRepository.existsById(id)) {
      throw new IllegalArgumentException(QUIZ_NOT_FOUND_MSG + id);
    }
    quizRepository.deleteById(id);
    log.info("Инвалидация кэша после удаления квиза ID: {}", id);
    cacheService.invalidate();
  }

  @Override
  public void invalidateCache() {
    cacheService.invalidate();
  }

  @Override
  @Transactional
  public int deleteByCategory(String category) {
    List<Quiz> quizzes = quizRepository.findByCategoryIgnoreCase(category);
    int count = quizzes.size();
    if (count > 0) {
      quizRepository.deleteAll(quizzes);
    }
    return count;
  }

  @Override
  @Transactional
  public int deleteByPublishedStatus(Boolean published) {
    List<Quiz> quizzes = quizRepository.findByIsPublished(published);
    int count = quizzes.size();
    if (count > 0) {
      quizRepository.deleteAll(quizzes);
    }
    return count;
  }

  @Override
  @Transactional
  public int deleteByTag(String tagName) {
    List<Quiz> quizzes = quizRepository.findByTag(tagName);
    int count = quizzes.size();
    if (count > 0) {
      quizRepository.deleteAll(quizzes);
    }
    return count;
  }

  @Override
  @Transactional
  public int deleteByMinQuestions(int minQuestions) {
    List<Quiz> allQuizzes = quizRepository.findAll();
    List<Quiz> toDelete = allQuizzes.stream()
        .filter(q -> q.getQuestions().size() < minQuestions)
        .toList();
    int count = toDelete.size();
    if (count > 0) {
      quizRepository.deleteAll(toDelete);
    }
    return count;
  }

  @Override
  @Transactional
  public long deleteAllQuizzes() {
    long count = quizRepository.count();
    quizRepository.deleteAll();
    return count;
  }

  @Override
  @Transactional
  public List<QuizResponse> createQuizzesBulk(List<QuizRequest> requests) {
    List<QuizResponse> responses = new ArrayList<>();
    for (int i = 0; i < requests.size(); i++) {
      if (i == 2) {
        throw new QuizServiceException("ROLLBACK: Error on 3rd quiz! Nothing will be saved.");
      }
      responses.add(createQuizInternal(requests.get(i)));
    }
    cacheService.invalidate();
    return responses;
  }

  @Override
  public List<QuizResponse> createQuizzesBulkWithoutTransaction(
      List<QuizRequest> requests) {
    List<QuizResponse> responses = new ArrayList<>();
    for (int i = 0; i < requests.size(); i++) {
      if (i == 2) {
        throw new QuizServiceException(
            "Error on 3rd quiz! First two were saved.");
      }
      responses.add(createQuizInternal(requests.get(i)));
    }
    cacheService.invalidate();
    return responses;
  }

  private QuizResponse createQuizInternal(QuizRequest request) {
    Quiz quiz = quizMapper.toEntity(request);
    quiz.setCreatedBy(getDefaultUser());

    if (request.tags() != null && !request.tags().isEmpty()) {
      List<Tag> processedTags = new ArrayList<>();

      for (String tagName : request.tags()) {
        // ВСЕГДА создаем новый тег, не ищем существующий
        Tag newTag = new Tag();
        newTag.setName(tagName + "_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 10000));
        processedTags.add(tagRepository.save(newTag));
      }
      quiz.setTags(new HashSet<>(processedTags));
    }

    Quiz savedQuiz = quizRepository.save(quiz);
    return quizMapper.toResponse(savedQuiz);
  }
}