package com.example.onlinetest.controller;

import com.example.onlinetest.dto.FullQuizRequest;
import com.example.onlinetest.dto.QuizRequest;
import com.example.onlinetest.dto.QuizResponse;
import com.example.onlinetest.service.QuizCacheService;
import com.example.onlinetest.service.QuizService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;
    private final QuizCacheService cacheService;

    @PostMapping
  public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/full")
  public ResponseEntity<QuizResponse> createFullQuiz(@Valid @RequestBody FullQuizRequest request) {
        QuizResponse response = quizService.createFullQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/full/without-transaction")
  public ResponseEntity<QuizResponse> createFullQuizWithoutTransaction(@Valid @RequestBody FullQuizRequest request) {
        QuizResponse response = quizService.createFullQuizWithoutTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
  public ResponseEntity<QuizResponse> getQuiz(@PathVariable Long id) {
        QuizResponse response = quizService.getQuiz(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/details")
  public ResponseEntity<QuizResponse> getQuizWithDetails(@PathVariable Long id) {
        QuizResponse response = quizService.getQuizWithDetails(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
  public ResponseEntity<List<QuizResponse>> getQuizzes(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean published,
        @RequestParam(required = false) String tag) {
        List<QuizResponse> responses = quizService.getAllQuizzes(category, published, tag);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/filter")
  public ResponseEntity<Page<QuizResponse>> getQuizzesWithFilters(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean published,
        @RequestParam(required = false) Integer minQuestions,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<QuizResponse> responses = quizService.getQuizzesWithFilters(category, published, minQuestions, pageable);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/filter/native")
  public ResponseEntity<Page<QuizResponse>> getQuizzesWithFiltersNative(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean published,
        @RequestParam(required = false) Integer minQuestions,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<QuizResponse> responses = quizService.getQuizzesWithFiltersNative(category, published,
            minQuestions, pageable);
        return ResponseEntity.ok(responses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(
        @PathVariable Long id,
          @Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.updateQuiz(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
      public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/category/{category}")
      public ResponseEntity<String> deleteQuizzesByCategory(@PathVariable String category) {
        int count = quizService.deleteByCategory(category);
        if (count == 0) {
            return ResponseEntity.notFound().build();
        }
        cacheService.invalidate();
        return ResponseEntity.ok("Deleted " + count + " quizzes with category: " + category);
    }

    @DeleteMapping("/published/{published}")
      public ResponseEntity<String> deleteQuizzesByPublishedStatus(@PathVariable boolean published) {
        int count = quizService.deleteByPublishedStatus(published);
        if (count == 0) {
            return ResponseEntity.notFound().build();
        }
        cacheService.invalidate();
        return ResponseEntity.ok("Deleted " + count + " quizzes with isPublished=" + published);
    }

    @DeleteMapping("/tag/{tagName}")
      public ResponseEntity<String> deleteQuizzesByTag(@PathVariable String tagName) {
        int count = quizService.deleteByTag(tagName);
        if (count == 0) {
            return ResponseEntity.notFound().build();
        }
        cacheService.invalidate();
        return ResponseEntity.ok("Deleted " + count + " quizzes with tag: " + tagName);
    }

    @DeleteMapping("/min-questions/{minQuestions}")
      public ResponseEntity<String> deleteQuizzesByMinQuestions(@PathVariable int minQuestions) {
        int count = quizService.deleteByMinQuestions(minQuestions);
        if (count == 0) {
            return ResponseEntity.notFound().build();
        }
        cacheService.invalidate();
        return ResponseEntity.ok("Deleted " + count + " quizzes with less than " + minQuestions + " questions");
    }

    @DeleteMapping("/all")
  public ResponseEntity<String> deleteAllQuizzes() {
        long count = quizService.deleteAllQuizzes();
        cacheService.invalidate();
        return ResponseEntity.ok("Deleted all " + count + " quizzes");
    }

    @DeleteMapping("/cache")
  public ResponseEntity<String> invalidateCache() {
        quizService.invalidateCache();
        return ResponseEntity.ok("Cache invalidated successfully!");
    }
}