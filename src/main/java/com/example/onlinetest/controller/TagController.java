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

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

  private final TagRepository tagRepository;
  private final TagMapper tagMapper;

  @GetMapping
  public ResponseEntity<List<TagResponse>> getAllTags() {
    List<TagResponse> responses = tagRepository.findAll().stream()
        .map(tagMapper::toResponse)
        .collect(Collectors.toList());
    return ResponseEntity.ok(responses);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TagResponse> getTagById(@PathVariable Long id) {
    return tagRepository.findById(id)
        .map(tagMapper::toResponse)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping
  public ResponseEntity<TagResponse> createTag(@RequestBody TagRequest request) {
    Tag tag = tagMapper.toEntity(request);
    Tag savedTag = tagRepository.save(tag);
    return ResponseEntity.ok(tagMapper.toResponse(savedTag));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
    if (tagRepository.existsById(id)) {
      tagRepository.deleteById(id);
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.notFound().build();
  }
}