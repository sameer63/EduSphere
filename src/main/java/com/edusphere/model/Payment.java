package com.edusphere.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Payment {

    private Long id;

    private Long studentId;

    private Long courseId;

    private BigDecimal amount;

    private String currency;

    private String status;

    private String transactionReference;

    private LocalDateTime createdAt;

    public Payment() {
    }

    public Payment(
            Long studentId,
            Long courseId,
            BigDecimal amount,
            String currency,
            String status,
            String transactionReference
    ) {
        this.studentId = studentId;
        this.courseId = courseId;
        this.amount = amount;
        this.currency = currency;
        this.status = status;
        this.transactionReference = transactionReference;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getCourseId() {
        return courseId;
    }

    public void setCourseId(Long courseId) {
        this.courseId = courseId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(
            String transactionReference
    ) {
        this.transactionReference = transactionReference;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}