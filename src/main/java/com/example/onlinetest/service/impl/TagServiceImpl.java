package com.example.onlinetest.service.impl;

import com.example.onlinetest.dto.TagRequest;
import com.example.onlinetest.dto.TagResponse;
import com.example.onlinetest.mapper.TagMapper;
import com.example.onlinetest.model.Tag;
import com.example.onlinetest.repository.TagRepository;
import com.example.onlinetest.service.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private static final String TAG_NOT_FOUND_MSG = "Tag not found with id: ";
    private static final String TAG_NOT_FOUND_NAME_MSG = "Tag not found with name: ";

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
  @Transactional
  public TagResponse createTag(TagRequest request) {
        if (tagRepository.findByName(request.name()).isPresent()) {
            throw new IllegalArgumentException("Tag already exists: " + request.name());
        }

        Tag tag = tagMapper.toEntity(request);
        Tag savedTag = tagRepository.save(tag);
        log.info("Tag created with id: {}, name: {}", savedTag.getId(), savedTag.getName());
        return tagMapper.toResponse(savedTag);
    }

    @Override
    @Transactional(readOnly = true)
  public TagResponse getTag(Long id) {
        return tagRepository.findById(id)
        .map(tagMapper::toResponse)
        .orElseThrow(() -> new IllegalArgumentException(TAG_NOT_FOUND_MSG + id));
    }

    @Override
  @Transactional(readOnly = true)
  public TagResponse getTagByName(String name) {
        return tagRepository.findByName(name)
        .map(tagMapper::toResponse)
        .orElseThrow(() -> new IllegalArgumentException(TAG_NOT_FOUND_NAME_MSG + name));
    }

    @Override
    @Transactional(readOnly = true)
  public List<TagResponse> getAllTags() {
        return tagRepository.findAll().stream()
        .map(tagMapper::toResponse)
        .toList();
    }

    @Override
    @Transactional
  public TagResponse updateTag(Long id, TagRequest request) {
        Tag tag = tagRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException(TAG_NOT_FOUND_MSG + id));

        if (tagRepository.findByName(request.name()).isPresent()
            && !tag.getName().equals(request.name())) {
            throw new IllegalArgumentException("Tag already exists: " + request.name());
        }

        tagMapper.update(tag, request);
        Tag updatedTag = tagRepository.save(tag);
        log.info("Tag updated with id: {}, name: {}", updatedTag.getId(), updatedTag.getName());
        return tagMapper.toResponse(updatedTag);
    }

    @Override
    @Transactional
  public void deleteTag(Long id) {
        if (!tagRepository.existsById(id)) {
            throw new IllegalArgumentException(TAG_NOT_FOUND_MSG + id);
        }
        tagRepository.deleteById(id);
        log.info("Tag deleted with id: {}", id);
    }

    @Override
  @Transactional
  public void deleteTagByName(String name) {
        Tag tag = tagRepository.findByName(name)
            .orElseThrow(() -> new IllegalArgumentException(TAG_NOT_FOUND_NAME_MSG + name));
        tagRepository.delete(tag);
        log.info("Tag deleted with name: {}", name);
    }
}