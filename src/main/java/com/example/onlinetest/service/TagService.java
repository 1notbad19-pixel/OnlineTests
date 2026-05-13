package com.example.onlinetest.service;

import com.example.onlinetest.dto.TagRequest;
import com.example.onlinetest.dto.TagResponse;
import java.util.List;

public interface TagService {

  TagResponse createTag(TagRequest request);

  TagResponse getTag(Long id);

  TagResponse getTagByName(String name);

  List<TagResponse> getAllTags();

  TagResponse updateTag(Long id, TagRequest request);

  void deleteTag(Long id);

  void deleteTagByName(String name);
}