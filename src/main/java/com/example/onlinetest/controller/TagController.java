package com.example.onlinetest.controller;

import com.example.onlinetest.dto.TagRequest;
import com.example.onlinetest.dto.TagResponse;
import com.example.onlinetest.service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

  private final TagService tagService;

  @PostMapping
  public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED).body(tagService.createTag(request));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TagResponse> getTag(@PathVariable Long id) {
    return ResponseEntity.ok(tagService.getTag(id));
  }

  @GetMapping("/name/{name}")
  public ResponseEntity<TagResponse> getTagByName(@PathVariable String name) {
    return ResponseEntity.ok(tagService.getTagByName(name));
  }

  @GetMapping
  public ResponseEntity<List<TagResponse>> getAllTags() {
    return ResponseEntity.ok(tagService.getAllTags());
  }

  @PutMapping("/{id}")
  public ResponseEntity<TagResponse> updateTag(@PathVariable Long id, @Valid @RequestBody TagRequest request) {
    return ResponseEntity.ok(tagService.updateTag(id, request));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
    tagService.deleteTag(id);
    return ResponseEntity.noContent().build();
  }

  @DeleteMapping("/name/{name}")
  public ResponseEntity<Void> deleteTagByName(@PathVariable String name) {
    tagService.deleteTagByName(name);
    return ResponseEntity.noContent().build();
  }
}