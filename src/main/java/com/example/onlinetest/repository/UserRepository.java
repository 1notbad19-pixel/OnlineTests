package com.example.onlinetest.repository;

import com.example.onlinetest.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(@NonNull String username);

    boolean existsByUsername(@NonNull String username);

    boolean existsByEmail(@NonNull String email);
}