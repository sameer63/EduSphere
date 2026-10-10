package com.edusphere.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class QuizAttemptSummaryDTO {

    private Long attemptId;
    private Long quizId;
    private Long studentId;

    private String studentName;
    private String studentEmail;

    private Integer attemptNumber;
    private BigDecimal score;

    private LocalDateTime startedAt;
    private LocalDateTime submittedAt;

    public QuizAttemptSummaryDTO() {
    }

    public QuizAttemptSummaryDTO(
            Long attemptId,
            Long quizId,
            Long studentId,
            String studentName,
            String studentEmail,
            Integer attemptNumber,
            BigDecimal score,
            LocalDateTime startedAt,
            LocalDateTime submittedAt
    ) {
        this.attemptId = attemptId;
        this.quizId = quizId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.studentEmail = studentEmail;
        this.attemptNumber = attemptNumber;
        this.score = score;
        this.startedAt = startedAt;
        this.submittedAt = submittedAt;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
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

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
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