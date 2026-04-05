package com.example.onlinetest.service;

import com.example.onlinetest.dto.QuizResponse;
import org.springframework.stereotype.Service;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizCacheService {

  // Ключ для кэша
  public static class CacheKey {
    private final String category;
    private final Boolean published;
    private final Integer minQuestions;
    private final int page;
    private final int size;

    public CacheKey(String category, Boolean published, Integer minQuestions, int page, int size) {
      this.category = category;
      this.published = published;
      this.minQuestions = minQuestions;
      this.page = page;
      this.size = size;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      CacheKey cacheKey = (CacheKey) o;
      return page == cacheKey.page &&
          size == cacheKey.size &&
          Objects.equals(category, cacheKey.category) &&
          Objects.equals(published, cacheKey.published) &&
          Objects.equals(minQuestions, cacheKey.minQuestions);
    }

    @Override
    public int hashCode() {
      return Objects.hash(category, published, minQuestions, page, size);
    }
  }

  private final ConcurrentHashMap<CacheKey, Object> cache = new ConcurrentHashMap<>();

  @SuppressWarnings("unchecked")
  public <T> T get(CacheKey key) {
    return (T) cache.get(key);
  }

  public void put(CacheKey key, Object value) {
    cache.put(key, value);
  }

  public void invalidate() {
    cache.clear();
  }

  public void invalidateByQuizId(Long quizId) {
    // При изменении квиза очищаем весь кэш (простая стратегия)
    cache.clear();
  }
}