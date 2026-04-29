package com.example.onlinetest.controller;

import com.example.onlinetest.dto.TagRequest;
import com.example.onlinetest.dto.TagResponse;
import com.example.onlinetest.mapper.TagMapper;
import com.example.onlinetest.model.Tag;
import com.example.onlinetest.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import com.example.onlinetest.exception.ErrorResponse;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Operation(summary = "Получить все теги", description = "Возвращает список всех тегов")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Успешно")
  })

    @GetMapping
  public ResponseEntity<List<TagResponse>> getAllTags() {
        List<TagResponse> responses = tagRepository.findAll().stream()
            .map(tagMapper::toResponse)
            .toList();
        return ResponseEntity.ok(responses);
    }
    @Operation(summary = "Получить тег по ID", description = "Возвращает тег с указанным ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Тег найден"),
      @ApiResponse(responseCode = "404", description = "Тег не найден",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })

    @GetMapping("/{id}")
  public ResponseEntity<TagResponse> getTagById(@PathVariable Long id) {
        return tagRepository.findById(id)
        .map(tagMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Создать новый тег", description = "Создаёт новый тег с указанным именем")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Тег создан"),
      @ApiResponse(responseCode = "400", description = "Неверные входные данные"),
      @ApiResponse(responseCode = "409", description = "Тег с таким именем уже существует",
          content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
  })
    @PostMapping
  public ResponseEntity<TagResponse> createTag(@RequestBody TagRequest request) {
        Tag tag = tagMapper.toEntity(request);
        Tag savedTag = tagRepository.save(tag);
        return ResponseEntity.ok(tagMapper.toResponse(savedTag));
    }

    @Operation(summary = "Удалить тег по ID", description = "Удаляет тег с указанным ID")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "204", description = "Тег удалён"),
      @ApiResponse(responseCode = "404", description = "Тег не найден")
  })
    @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        if (tagRepository.existsById(id)) {
            tagRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}