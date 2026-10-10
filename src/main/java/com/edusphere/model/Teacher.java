package com.edusphere.model;

import java.time.LocalDateTime;

public class Teacher {
    private Long id;
    private Long userId;
    private String qualification;
    private String bio;
    private LocalDateTime createdAt;

    public Teacher(){}
    public Teacher(Long id,
                   Long userId,
                   String qualification,
                   String bio,
                   LocalDateTime createdAt){
        this.id=id;
        this.userId=userId;
        this.qualification=qualification;
        this.bio=bio;
        this.createdAt=createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
