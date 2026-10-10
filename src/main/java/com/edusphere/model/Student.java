package com.edusphere.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Student {
    private Long id;
    private Long userId;
    private LocalDate dateOfBirth;
    private String phone;
    private LocalDateTime createdAt;

    public Student(){}

    public Student(Long id,
                   Long userId,
                   LocalDate dateOfBirth,
                   String phone,
                   LocalDateTime createdAt){
        this.id=id;
        this.userId=userId;
        this.dateOfBirth=dateOfBirth;
        this.phone=phone;
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

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
