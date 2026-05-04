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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
@EqualsAndHashCode(exclude = {"quizzes"})
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

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  private List<Quiz> quizzes = new ArrayList<>();

}