package com.example.onlinetest.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
public class Quiz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String category;

    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;

    @Column(name = "max_attempts")
    private Integer maxAttempts;

    @Column(name = "is_published")
    private Boolean isPublished = false;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ManyToMany с Tag
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
        name = "quiz_tag_mapping",
        joinColumns = @JoinColumn(name = "quiz_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags = new ArrayList<>();

    // OneToMany с Question
    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Question> questions = new ArrayList<>();

    // Конструкторы
    public Quiz() { }

    public Quiz(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id; }
    public void setId(Long id) {
        this.id = id; }

    public String getTitle() {
        return title; }
    public void setTitle(String title) {
        this.title = title; }

    public String getDescription() {
        return description; }
    public void setDescription(String description) {
        this.description = description; }

    public String getCategory() {
        return category; }
    public void setCategory(String category) {
        this.category = category; }

    public Integer getTimeLimitMinutes() {
        return timeLimitMinutes; }
    public void setTimeLimitMinutes(Integer timeLimitMinutes) {
        this.timeLimitMinutes = timeLimitMinutes; }

    public Integer getMaxAttempts() {
        return maxAttempts; }
    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts; }

    public Boolean getIsPublished() {
        return isPublished; }
    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished; }

    public Integer getPassingScore() {
        return passingScore; }
    public void setPassingScore(Integer passingScore) {
        this.passingScore = passingScore; }

    public LocalDateTime getCreatedAt() {
        return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() {
        return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt; }

    public List<Tag> getTags() {
        return tags; }
    public void setTags(List<Tag> tags) {
        this.tags = tags; }

    public List<Question> getQuestions() {
        return questions; }
    public void setQuestions(List<Question> questions) {
        this.questions = questions; }
}