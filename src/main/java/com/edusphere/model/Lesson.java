package com.edusphere.model;

import java.time.LocalDateTime;

public class Lesson {

    private Long id;
    private Long courseId;
    private String title;
    private String content;
    private Integer position;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Lesson() {
    }

    public Lesson(
            Long courseId,
            String title,
            String content,
            Integer position) {

        this.courseId = courseId;
        this.title = title;
        this.content = content;
        this.position = position;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}