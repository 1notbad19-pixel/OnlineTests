package com.example.onlinetest.controller;

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

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagRepository tagRepository;

    @GetMapping
  public ResponseEntity<List<Tag>> getAllTags() {
        return ResponseEntity.ok(tagRepository.findAll());
    }

    @GetMapping("/{id}")
  public ResponseEntity<Tag> getTagById(@PathVariable Long id) {
        return tagRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
  public ResponseEntity<Tag> createTag(@RequestBody Tag tag) {
        Tag savedTag = tagRepository.save(tag);
        return ResponseEntity.ok(savedTag);
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