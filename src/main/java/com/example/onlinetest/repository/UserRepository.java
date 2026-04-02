package com.example.onlinetest.repository;

import com.example.onlinetest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Пустой интерфейс - базовые методы от JpaRepository уже есть
    // findAll(), findById(), save(), delete() и т.д.
}