package com.example.onlinetest.repository;

import com.example.onlinetest.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByName(String name);

    @Query("SELECT t FROM Tag t LEFT JOIN FETCH t.quizzes WHERE t.id = :id")
  Optional<Tag> findByIdWithQuizzes(@Param("id") Long id);

    List<Tag> findAllByNameIn(List<String> names);
}