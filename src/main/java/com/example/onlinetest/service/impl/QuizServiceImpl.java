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
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
  public QuizResponse createQuiz(QuizRequest request) {
        Quiz quiz = quizMapper.toEntity(request);
        quiz.setCreatedBy(getDefaultUser());

        if (request.tags() != null && !request.tags().isEmpty()) {
            List<Tag> processedTags = request.tags().stream()
                .map(tagName -> tagRepository.findByName(tagName)
                  .orElseGet(() -> {
                      Tag newTag = new Tag();
                      newTag.setName(tagName);
                      return tagRepository.save(newTag);
                  }))
                .toList();
            quiz.setTags(new ArrayList<>(processedTags));
        }

        Quiz savedQuiz = quizRepository.save(quiz);
        cacheService.invalidate();
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

            if (request.questions().size() > 2) {
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

            if (request.questions().size() > 2) {
                throw new QuizServiceException("Too many questions! Quiz saved but questions were not.");
            }
        }

        cacheService.invalidate();
        return quizMapper.toResponse(savedQuiz);
    }
    @Override
    @Transactional(readOnly = true)
  public QuizResponse getQuiz(Long id) {
        log.info("N+1 PROBLEM: This will execute multiple SQL queries");
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(QUIZ_NOT_FOUND_MSG + id));
        return quizMapper.toResponse(quiz);
    }

    @Override
    @Transactional(readOnly = true)
  public QuizResponse getQuizWithDetails(Long id) {
        log.info("N+1 SOLUTION: This will execute only ONE SQL query with JOIN FETCH");
        Quiz quiz = quizRepository.findByIdWithQuestions(id)
            .orElseThrow(() -> new IllegalArgumentException(QUIZ_NOT_FOUND_MSG + id));
        return quizMapper.toResponse(quiz);
    }

    @Override
    @Transactional(readOnly = true)
  public List<QuizResponse> getAllQuizzes(String category, Boolean published, String tag) {
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
    public Page<QuizResponse> getQuizzesWithFilters(String category, Boolean published,
          Integer minQuestions, Pageable pageable) {
        Page<Quiz> quizPage = quizRepository.findQuizzesWithFilters(category, published, minQuestions, pageable);
        return quizPage.map(quizMapper::toResponse);
    }

    @Override
  @Transactional(readOnly = true)
    public Page<QuizResponse> getQuizzesWithFiltersNative(String category, Boolean published,
          Integer minQuestions, Pageable pageable) {
        Page<Quiz> quizPage = quizRepository.findQuizzesWithFiltersNative(category, published, minQuestions, pageable);
        return quizPage.map(quizMapper::toResponse);
    }

    @Override
    @Transactional
  public QuizResponse updateQuiz(Long id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(QUIZ_NOT_FOUND_MSG + id));
        quizMapper.update(quiz, request);
        Quiz updatedQuiz = quizRepository.save(quiz);
        cacheService.invalidate();
        return quizMapper.toResponse(updatedQuiz);
    }

    @Override
    @Transactional
  public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new IllegalArgumentException(QUIZ_NOT_FOUND_MSG + id);
        }
        quizRepository.deleteById(id);
        cacheService.invalidate();
    }

    @Override
  public void invalidateCache() {
        cacheService.invalidate();
    }

    @Override
  public int deleteByCategory(String category) {
        List<Quiz> quizzes = quizRepository.findByCategoryIgnoreCase(category);
        int count = quizzes.size();
        if (count > 0) {
            quizRepository.deleteAll(quizzes);
        }
        return count;
    }

    @Override
  public int deleteByPublishedStatus(Boolean published) {
        List<Quiz> quizzes = quizRepository.findByIsPublished(published);
        int count = quizzes.size();
        if (count > 0) {
            quizRepository.deleteAll(quizzes);
        }
        return count;
    }

    @Override
  public int deleteByTag(String tagName) {
        List<Quiz> quizzes = quizRepository.findByTag(tagName);
        int count = quizzes.size();
        if (count > 0) {
            quizRepository.deleteAll(quizzes);
        }
        return count;
    }

    @Override
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
  public long deleteAllQuizzes() {
        long count = quizRepository.count();
        quizRepository.deleteAll();
        return count;
    }
}