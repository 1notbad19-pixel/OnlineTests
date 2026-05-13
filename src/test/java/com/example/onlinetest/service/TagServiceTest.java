package com.example.onlinetest.service;

import com.example.onlinetest.dto.TagRequest;
import com.example.onlinetest.dto.TagResponse;
import com.example.onlinetest.mapper.TagMapper;
import com.example.onlinetest.model.Tag;
import com.example.onlinetest.repository.TagRepository;
import com.example.onlinetest.service.impl.TagServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceTest {

  @Mock
  private TagRepository tagRepository;

  @Mock
  private TagMapper tagMapper;

  @InjectMocks
  private TagServiceImpl tagService;

  private Tag testTag;
  private TagRequest testRequest;
  private TagResponse testResponse;

  @BeforeEach
  void setUp() {
    testTag = new Tag();
    testTag.setId(1L);
    testTag.setName("java");

    testRequest = new TagRequest("java");
    testResponse = new TagResponse(1L, "java");
  }

  @Test
  void createTag_Success_ReturnsTagResponse() {
    when(tagRepository.findByName("java")).thenReturn(Optional.empty());
    when(tagMapper.toEntity(testRequest)).thenReturn(testTag);
    when(tagRepository.save(any(Tag.class))).thenReturn(testTag);
    when(tagMapper.toResponse(testTag)).thenReturn(testResponse);

    TagResponse result = tagService.createTag(testRequest);

    assertNotNull(result);
    assertEquals("java", result.name());
    verify(tagRepository).save(testTag);
  }

  @Test
  void createTag_DuplicateName_ThrowsException() {
    when(tagRepository.findByName("java")).thenReturn(Optional.of(testTag));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> tagService.createTag(testRequest));

    assertEquals("Tag already exists: java", exception.getMessage());
    verify(tagRepository, never()).save(any());
  }

  @Test
  void updateTag_Success_ReturnsUpdatedTag() {
    TagRequest updateRequest = new TagRequest("spring");
    Tag updatedTag = new Tag();
    updatedTag.setId(1L);
    updatedTag.setName("spring");
    TagResponse updatedResponse = new TagResponse(1L, "spring");

    when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
    when(tagRepository.findByName("spring")).thenReturn(Optional.empty());
    // ИСПРАВЛЕНО: возвращаем обновлённый тег
    when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);
    when(tagMapper.toResponse(updatedTag)).thenReturn(updatedResponse);

    TagResponse result = tagService.updateTag(1L, updateRequest);

    assertNotNull(result);
    assertEquals("spring", result.name());
    verify(tagRepository).save(any(Tag.class));
  }

  @Test
  void updateTag_TagNotFound_ThrowsException() {
    when(tagRepository.findById(999L)).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> tagService.updateTag(999L, testRequest));

    assertEquals("Tag not found with id: 999", exception.getMessage());
    verify(tagRepository, never()).save(any());
  }

  @Test
  void updateTag_DuplicateName_ThrowsException() {
    Tag existingTag = new Tag();
    existingTag.setId(2L);
    existingTag.setName("spring");

    TagRequest updateRequest = new TagRequest("spring");

    when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
    when(tagRepository.findByName("spring")).thenReturn(Optional.of(existingTag));

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> tagService.updateTag(1L, updateRequest));

    assertEquals("Tag already exists: spring", exception.getMessage());
    verify(tagRepository, never()).save(any());
  }

  @Test
  void updateTag_SameName_NoDuplicateError() {
    TagRequest updateRequest = new TagRequest("java");

    when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
    when(tagRepository.findByName("java")).thenReturn(Optional.of(testTag));
    when(tagRepository.save(any(Tag.class))).thenReturn(testTag);
    when(tagMapper.toResponse(testTag)).thenReturn(testResponse);

    TagResponse result = tagService.updateTag(1L, updateRequest);

    assertNotNull(result);
    verify(tagRepository).save(testTag);
  }

  @Test
  void deleteTag_Success_DeletesTag() {
    when(tagRepository.existsById(1L)).thenReturn(true);
    doNothing().when(tagRepository).deleteById(1L);

    assertDoesNotThrow(() -> tagService.deleteTag(1L));
    verify(tagRepository).deleteById(1L);
  }

  @Test
  void deleteTag_NotFound_ThrowsException() {
    when(tagRepository.existsById(999L)).thenReturn(false);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> tagService.deleteTag(999L));

    assertEquals("Tag not found with id: 999", exception.getMessage());
    verify(tagRepository, never()).deleteById(any());
  }

  @Test
  void deleteTagByName_Success_DeletesTag() {
    when(tagRepository.findByName("java")).thenReturn(Optional.of(testTag));
    doNothing().when(tagRepository).delete(testTag);

    assertDoesNotThrow(() -> tagService.deleteTagByName("java"));
    verify(tagRepository).delete(testTag);
  }

  @Test
  void deleteTagByName_NotFound_ThrowsException() {
    when(tagRepository.findByName("unknown")).thenReturn(Optional.empty());

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> tagService.deleteTagByName("unknown"));

    assertEquals("Tag not found with name: unknown", exception.getMessage());
    verify(tagRepository, never()).delete(any());
  }

  @Test
  void getTag_Success_ReturnsTagResponse() {
    when(tagRepository.findById(1L)).thenReturn(Optional.of(testTag));
    when(tagMapper.toResponse(testTag)).thenReturn(testResponse);

    TagResponse result = tagService.getTag(1L);

    assertNotNull(result);
    assertEquals(1L, result.id());
  }

  @Test
  void getTag_NotFound_ThrowsException() {
    when(tagRepository.findById(999L)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> tagService.getTag(999L));
  }

  @Test
  void getTagByName_Success_ReturnsTagResponse() {
    when(tagRepository.findByName("java")).thenReturn(Optional.of(testTag));
    when(tagMapper.toResponse(testTag)).thenReturn(testResponse);

    TagResponse result = tagService.getTagByName("java");

    assertNotNull(result);
    assertEquals("java", result.name());
  }

  @Test
  void getTagByName_NotFound_ThrowsException() {
    when(tagRepository.findByName("unknown")).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> tagService.getTagByName("unknown"));
  }

  @Test
  void getAllTags_ReturnsList() {
    when(tagRepository.findAll()).thenReturn(List.of(testTag));
    when(tagMapper.toResponse(testTag)).thenReturn(testResponse);

    List<TagResponse> result = tagService.getAllTags();

    assertFalse(result.isEmpty());
    assertEquals(1, result.size());
  }
}