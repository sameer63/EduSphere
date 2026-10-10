package com.edusphere.model;

import java.time.LocalDateTime;

public class Quiz {

    private Long id;
    private Long courseId;
    private String title;
    private String description;
    private Integer maxAttempts;
    private LocalDateTime createdAt;

    public Quiz() {
    }

    public Quiz(Long courseId,
                String title,
                String description,
                Integer maxAttempts) {

        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.maxAttempts = maxAttempts;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(Integer maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}