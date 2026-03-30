package com.example.onlinetest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

    @Column(nullable = false, unique = true)
  private String username;

    @Column(nullable = false, unique = true)
  private String email;

    @Column(nullable = false)
  private String password;

    @Column(name = "first_name")
  private String firstName;

    @Column(name = "last_name")
  private String lastName;

    @Column(name = "created_at")
  private LocalDateTime createdAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
  private List<QuizAttempt> attempts = new ArrayList<>();
}