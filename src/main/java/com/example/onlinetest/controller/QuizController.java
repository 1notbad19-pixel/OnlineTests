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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.example.onlinetest.exception.ErrorResponse;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private static final String DELETED_MESSAGE_PREFIX = "Deleted ";
    private static final String QUIZZES = " quizzes";

    private final QuizService quizService;
    private final QuizCacheService cacheService;
    @Operation(summary = "Создать новый квиз", description = "Создаёт новый квиз с указанными параметрами")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Квиз успешно создан"),
        @ApiResponse(responseCode = "400", description = "Неверные входные данные",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "409", description = "Конфликт (дубликат тега)",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
  public ResponseEntity<QuizResponse> createQuiz(@Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.createQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(summary = "Создать полный квиз", description = "Создаёт квиз с вопросами и ответами (с транзакцией)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Квиз успешно создан"),
      @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
      @ApiResponse(responseCode = "500", description = "Ошибка сервера (откат транзакции)")
  })

    @PostMapping("/full")
  public ResponseEntity<QuizResponse> createFullQuiz(@Valid @RequestBody FullQuizRequest request) {
        QuizResponse response = quizService.createFullQuiz(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(summary = "Создать полный квиз без транзакции",
        description = "Демонстрация частичного сохранения при ошибке")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Квиз создан (вопросы могут не сохраниться)"),
      @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
      @ApiResponse(responseCode = "500", description = "Ошибка сервера (квиз сохранился, вопросы нет)")
  })
    @PostMapping("/full/without-transaction")
  public ResponseEntity<QuizResponse> createFullQuizWithoutTransaction(@Valid @RequestBody FullQuizRequest request) {
        QuizResponse response = quizService.createFullQuizWithoutTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @Operation(summary = "Получить квиз по ID",
        description = "Возвращает квиз с указанным ID (демонстрация N+1 проблемы)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Квиз найден"),
      @ApiResponse(responseCode = "404", description = "Квиз не найден",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
    @GetMapping("/{id}")
  public ResponseEntity<QuizResponse> getQuiz(@PathVariable Long id) {
        QuizResponse response = quizService.getQuiz(id);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Получить квиз с деталями", description = "Возвращает квиз с вопросами (решение N+1 проблемы)")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Квиз найден"),
      @ApiResponse(responseCode = "404", description = "Квиз не найден",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
    @GetMapping("/{id}/details")
  public ResponseEntity<QuizResponse> getQuizWithDetails(@PathVariable Long id) {
        QuizResponse response = quizService.getQuizWithDetails(id);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Получить все квизы", description = "Возвращает список всех квизов с возможностью фильтрации")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Успешно"),
      @ApiResponse(responseCode = "400", description = "Неверные параметры фильтрации")
  })
    @GetMapping
  public ResponseEntity<List<QuizResponse>> getQuizzes(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) Boolean published,
        @RequestParam(required = false) String tag) {
        List<QuizResponse> responses = quizService.getAllQuizzes(category, published, tag);
        return ResponseEntity.ok(responses);
    }
    @Operation(summary = "Фильтрация квизов с пагинацией (JPQL)",
        description = "Сложный запрос с фильтрацией по вложенным сущностям")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Успешно"),
      @ApiResponse(responseCode = "400", description = "Неверные параметры фильтрации")
  })
    @GetMapping("/filter")
  public ResponseEntity<Page<QuizResponse>> getQuizzesWithFilters(
        @Parameter(description = "Фильтр по категории", example = "Programming")
        @RequestParam(required = false) String category,
        @Parameter(description = "Фильтр по статусу публикации", example = "true")
        @RequestParam(required = false) Boolean published,
        @Parameter(description = "Минимальное количество вопросов", example = "2")
        @RequestParam(required = false) Integer minQuestions,
        @Parameter(description = "Номер страницы (с 0)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Размер страницы", example = "10")
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<QuizResponse> responses = quizService.getQuizzesWithFilters(category, published, minQuestions, pageable);
        return ResponseEntity.ok(responses);
    }
    @Operation(summary = "Фильтрация квизов с пагинацией (Native SQL)", description = "То же самое, но на чистом SQL")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Успешно"),
      @ApiResponse(responseCode = "400", description = "Неверные параметры фильтрации")
  })

    @GetMapping("/filter/native")
  public ResponseEntity<Page<QuizResponse>> getQuizzesWithFiltersNative(
        @Parameter(description = "Фильтр по категории", example = "Programming")
        @RequestParam(required = false) String category,
        @Parameter(description = "Фильтр по статусу публикации", example = "true")
        @RequestParam(required = false) Boolean published,
        @Parameter(description = "Минимальное количество вопросов", example = "2")
        @RequestParam(required = false) Integer minQuestions,
        @Parameter(description = "Номер страницы (с 0)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Размер страницы", example = "10")
        @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());
        Page<QuizResponse> responses = quizService.getQuizzesWithFiltersNative(category, published,
            minQuestions, pageable);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Обновить квиз", description = "Обновляет существующий квиз по ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Квиз обновлён"),
      @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
      @ApiResponse(responseCode = "404", description = "Квиз не найден",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
    @PutMapping("/{id}")
    public ResponseEntity<QuizResponse> updateQuiz(
        @Parameter(description = "ID квиза", example = "1")
        @PathVariable Long id,
          @Valid @RequestBody QuizRequest request) {
        QuizResponse response = quizService.updateQuiz(id, request);
        return ResponseEntity.ok(response);
    }
    @Operation(summary = "Удалить квиз по ID", description = "Удаляет квиз с указанным ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Квиз удалён"),
      @ApiResponse(responseCode = "404", description = "Квиз не найден",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
    @DeleteMapping("/{id}")
      public ResponseEntity<Void> deleteQuiz(@PathVariable Long id) {
        quizService.deleteQuiz(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Удалить квизы по категории", description = "Удаляет все квизы указанной категории")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Квизы удалены"),
      @ApiResponse(responseCode = "404", description = "Категория не найдена")
  })
    @DeleteMapping("/category/{category}")
      public ResponseEntity<String> deleteQuizzesByCategory(@PathVariable String category) {
        int count = quizService.deleteByCategory(category);
        if (count == 0) {
            return ResponseEntity.notFound().build();
        }
        cacheService.invalidate();
        return ResponseEntity.ok(DELETED_MESSAGE_PREFIX + count + QUIZZES + " with category: " + category);
    }
    @Operation(summary = "Удалить квизы по статусу публикации",
        description = "Удаляет все опубликованные или черновики")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Квизы удалены"),
      @ApiResponse(responseCode = "404", description = "Статус не найден")
  })
    @DeleteMapping("/published/{published}")
      public ResponseEntity<String> deleteQuizzesByPublishedStatus(@PathVariable boolean published) {
        int count = quizService.deleteByPublishedStatus(published);
        if (count == 0) {
            return ResponseEntity.notFound().build();
        }
        cacheService.invalidate();
        return ResponseEntity.ok(DELETED_MESSAGE_PREFIX + count + QUIZZES + " with isPublished=" + published);
    }
    @Operation(summary = "Удалить квизы по тегу", description = "Удаляет все квизы с указанным тегом")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Квизы удалены"),
      @ApiResponse(responseCode = "404", description = "Тег не найден")
  })
    @DeleteMapping("/tag/{tagName}")
      public ResponseEntity<String> deleteQuizzesByTag(@PathVariable String tagName) {
        int count = quizService.deleteByTag(tagName);
        if (count == 0) {
            return ResponseEntity.notFound().build();
        }
        cacheService.invalidate();
        return ResponseEntity.ok(DELETED_MESSAGE_PREFIX + count + QUIZZES + " with tag: " + tagName);
    }

    @Operation(summary = "Удалить квизы с малым количеством вопросов",
        description = "Удаляет квизы, у которых меньше указанного числа вопросов")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Квизы удалены"),
      @ApiResponse(responseCode = "404", description = "Квизы не найдены")
  })
    @DeleteMapping("/min-questions/{minQuestions}")
      public ResponseEntity<String> deleteQuizzesByMinQuestions(@PathVariable int minQuestions) {
        int count = quizService.deleteByMinQuestions(minQuestions);
        if (count == 0) {
            return ResponseEntity.notFound().build();
        }
        cacheService.invalidate();
        return ResponseEntity.ok(DELETED_MESSAGE_PREFIX +
            count + QUIZZES + " with less than " + minQuestions + " questions");
    }

    @Operation(summary = "Удалить все квизы", description = "Полная очистка таблицы квизов")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Все квизы удалены")
  })
    @DeleteMapping("/all")
  public ResponseEntity<String> deleteAllQuizzes() {
        long count = quizService.deleteAllQuizzes();
        cacheService.invalidate();
        return ResponseEntity.ok(DELETED_MESSAGE_PREFIX + count + QUIZZES + " (all)");
    }

    @Operation(summary = "Очистить кэш", description = "Инвалидация кэша фильтрации")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Кэш очищен")
  })
    @DeleteMapping("/cache")
  public ResponseEntity<String> invalidateCache() {
        quizService.invalidateCache();
        return ResponseEntity.ok("Cache invalidated successfully!");
    }
}