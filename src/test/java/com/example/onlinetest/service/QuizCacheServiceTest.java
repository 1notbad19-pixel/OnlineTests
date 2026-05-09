package com.example.onlinetest.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QuizCacheServiceTest {

  private final QuizCacheService cacheService = new QuizCacheService();

  @Test
  void cacheKey_EqualsAndHashCode() {
    QuizCacheService.CacheKey key1 = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    QuizCacheService.CacheKey key2 = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    QuizCacheService.CacheKey key3 = new QuizCacheService.CacheKey("Database", true, 2, 0, 10);

    assertEquals(key1, key2);
    assertEquals(key1.hashCode(), key2.hashCode());
    assertNotEquals(key1, key3);
  }

  @Test
  void cacheKey_Equals_SameObject_ReturnsTrue() {
    QuizCacheService.CacheKey key = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    assertEquals(key, key);
  }

  @Test
  void cacheKey_Equals_Null_ReturnsFalse() {
    QuizCacheService.CacheKey key = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    assertFalse(key.equals(null));
  }

  @Test
  void cacheKey_Equals_DifferentClass_ReturnsFalse() {
    QuizCacheService.CacheKey key = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    assertFalse(key.equals("some string"));
  }

  @Test
  void cacheKey_Equals_DifferentPublished_ReturnsFalse() {
    QuizCacheService.CacheKey key1 = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    QuizCacheService.CacheKey key2 = new QuizCacheService.CacheKey("Programming", false, 2, 0, 10);
    assertNotEquals(key1, key2);
  }

  @Test
  void cacheKey_Equals_DifferentMinQuestions_ReturnsFalse() {
    QuizCacheService.CacheKey key1 = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    QuizCacheService.CacheKey key2 = new QuizCacheService.CacheKey("Programming", true, 5, 0, 10);
    assertNotEquals(key1, key2);
  }

  @Test
  void cacheKey_Equals_DifferentPage_ReturnsFalse() {
    QuizCacheService.CacheKey key1 = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    QuizCacheService.CacheKey key2 = new QuizCacheService.CacheKey("Programming", true, 2, 1, 10);
    assertNotEquals(key1, key2);
  }

  @Test
  void cacheKey_Equals_DifferentSize_ReturnsFalse() {
    QuizCacheService.CacheKey key1 = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    QuizCacheService.CacheKey key2 = new QuizCacheService.CacheKey("Programming", true, 2, 0, 20);
    assertNotEquals(key1, key2);
  }

  @Test
  void putAndGet() {
    QuizCacheService.CacheKey key = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    String value = "test data";

    cacheService.put(key, value);
    String cached = cacheService.get(key);

    assertEquals(value, cached);
  }

  @Test
  void invalidate_ClearsCache() {
    QuizCacheService.CacheKey key = new QuizCacheService.CacheKey("Programming", true, 2, 0, 10);
    cacheService.put(key, "data");

    cacheService.invalidate();

    assertNull(cacheService.get(key));
  }
}