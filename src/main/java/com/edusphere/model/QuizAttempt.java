package com.edusphere.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuizAttempt {

    private Long id;
    private Long quizId;
    private Long studentId;
    private Integer attemptNumber;
    private BigDecimal score;
    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;

    public QuizAttempt() {
    }

    public QuizAttempt(Long quizId,
                       Long studentId,
                       Integer attemptNumber) {

        this.quizId = quizId;
        this.studentId = studentId;
        this.attemptNumber = attemptNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Integer getAttemptNumber() {
        return attemptNumber;
    }

    public void setAttemptNumber(Integer attemptNumber) {
        this.attemptNumber = attemptNumber;
    }

    public BigDecimal getScore() {
        return score;
    }

    public void setScore(BigDecimal score) {
        this.score = score;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}