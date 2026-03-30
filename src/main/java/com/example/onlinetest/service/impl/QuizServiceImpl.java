package com.example.onlinetest.service.impl;

import com.example.onlinetest.dto.FullQuizRequest;
import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import com.example.onlinetest.mapper.QuestionMapper;
import com.example.onlinetest.mapper.QuizMapper;
import com.example.onlinetest.model.Question;
import com.example.onlinetest.model.Quiz;
import com.example.onlinetest.model.Tag;
import com.example.onlinetest.repository.QuizRepository;
import com.example.onlinetest.repository.TagRepository;
import com.example.onlinetest.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final TagRepository tagRepository;
    private final QuizMapper quizMapper;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
  public QuizResponse createQuiz(QuizRequest request) {
        Quiz quiz = quizMapper.toEntity(request);

    // Обработка тегов: ищем существующие или создаем новые
        if (request.tags() != null && !request.tags().isEmpty()) {
            List<Tag> processedTags = request.tags().stream()
                .map(tagName -> tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    return tagRepository.save(newTag);
                }))
                .collect(Collectors.toList());
            quiz.setTags(processedTags);
        }

        Quiz savedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponse(savedQuiz);
    }

    @Override
    @Transactional
  public QuizResponse createFullQuiz(FullQuizRequest request) {
        Quiz quiz = quizMapper.toEntity(request.quiz());
        Quiz savedQuiz = quizRepository.save(quiz);

        if (request.questions() != null && !request.questions().isEmpty()) {
            final Quiz finalQuiz = savedQuiz;

            List<Question> questions = request.questions().stream()
                .map(questionMapper::toEntity)
                .peek(question -> question.setQuiz(finalQuiz))
                .collect(Collectors.toList());

            savedQuiz.setQuestions(questions);
            savedQuiz = quizRepository.save(savedQuiz);
        }

        return quizMapper.toResponse(savedQuiz);
    }

    @Override
  public QuizResponse createFullQuizWithoutTransaction(FullQuizRequest request) {
        Quiz quiz = quizMapper.toEntity(request.quiz());
        Quiz savedQuiz = quizRepository.save(quiz);

        if (request.questions() != null && !request.questions().isEmpty()) {
            final Quiz finalQuiz = savedQuiz;

            List<Question> questions = request.questions().stream()
                .map(questionMapper::toEntity)
                .peek(question -> question.setQuiz(finalQuiz))
                .collect(Collectors.toList());

            savedQuiz.setQuestions(questions);

      // Демонстрация частичного сохранения - при ошибке квиз сохранится, а вопросы нет
            if (request.questions().size() > 2) {
                throw new RuntimeException("Simulated error: Too many questions! Quiz saved but questions were not.");
            }
        }

        return quizMapper.toResponse(savedQuiz);
    }

    @Override
    @Transactional(readOnly = true)
  public QuizResponse getQuiz(Long id) {
        return quizRepository.findById(id)
        .map(quizMapper::toResponse)
        .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
  public QuizResponse getQuizWithDetails(Long id) {
        Quiz quiz = quizRepository.findByIdWithQuestionsAndAnswers(id)
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));

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
        .collect(Collectors.toList());
    }

    @Override
    @Transactional
  public QuizResponse updateQuiz(Long id, QuizRequest request) {
        Quiz quiz = quizRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Quiz not found with id: " + id));

        quizMapper.update(quiz, request);
        Quiz updatedQuiz = quizRepository.save(quiz);
        return quizMapper.toResponse(updatedQuiz);
    }

    @Override
    @Transactional
  public void deleteQuiz(Long id) {
        if (!quizRepository.existsById(id)) {
            throw new IllegalArgumentException("Quiz not found with id: " + id);
        }
        quizRepository.deleteById(id);
    }
}