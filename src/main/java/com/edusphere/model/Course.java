package com.edusphere.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Course {
    private Long id;
    private Long teacherId;
    private Long categoryId;

    private String title;
    private String description;

    private BigDecimal price;
    private String status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Course(){}
    public Course(Long id,
                  Long teacherId,
                  Long categoryId,
                  String title,
                  String description,
                  BigDecimal price,
                  String status,
                  LocalDateTime createdAt,
                  LocalDateTime updatedAt){
        this.id=id;
        this.teacherId=teacherId;
        this.categoryId=categoryId;
        this.title=title;
        this.description=description;
        this.price=price;
        this.status=status;
        this.createdAt=createdAt;
        this.updatedAt=updatedAt;
    }
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(Long teacherId) {
        this.teacherId = teacherId;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
