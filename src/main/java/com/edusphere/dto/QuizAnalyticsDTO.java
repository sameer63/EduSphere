package com.edusphere.dto;

import java.math.BigDecimal;

public class QuizAnalyticsDTO {

    private Long quizId;

    private long totalAttempts;
    private long submittedAttempts;
    private long inProgressAttempts;

    private BigDecimal averageScore;
    private BigDecimal highestScore;
    private BigDecimal lowestScore;

    private BigDecimal averagePercentage;
    private BigDecimal passPercentage;

    public QuizAnalyticsDTO() {
    }

    public QuizAnalyticsDTO(
            Long quizId,
            long totalAttempts,
            long submittedAttempts,
            long inProgressAttempts,
            BigDecimal averageScore,
            BigDecimal highestScore,
            BigDecimal lowestScore,
            BigDecimal averagePercentage,
            BigDecimal passPercentage
    ) {
        this.quizId = quizId;
        this.totalAttempts = totalAttempts;
        this.submittedAttempts = submittedAttempts;
        this.inProgressAttempts = inProgressAttempts;
        this.averageScore = averageScore;
        this.highestScore = highestScore;
        this.lowestScore = lowestScore;
        this.averagePercentage = averagePercentage;
        this.passPercentage = passPercentage;
    }

    public Long getQuizId() {
        return quizId;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public long getTotalAttempts() {
        return totalAttempts;
    }

    public void setTotalAttempts(long totalAttempts) {
        this.totalAttempts = totalAttempts;
    }

    public long getSubmittedAttempts() {
        return submittedAttempts;
    }

    public void setSubmittedAttempts(long submittedAttempts) {
        this.submittedAttempts = submittedAttempts;
    }

    public long getInProgressAttempts() {
        return inProgressAttempts;
    }

    public void setInProgressAttempts(long inProgressAttempts) {
        this.inProgressAttempts = inProgressAttempts;
    }

    public BigDecimal getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(BigDecimal averageScore) {
        this.averageScore = averageScore;
    }

    public BigDecimal getHighestScore() {
        return highestScore;
    }

    public void setHighestScore(BigDecimal highestScore) {
        this.highestScore = highestScore;
    }

    public BigDecimal getLowestScore() {
        return lowestScore;
    }

    public void setLowestScore(BigDecimal lowestScore) {
        this.lowestScore = lowestScore;
    }

    public BigDecimal getAveragePercentage() {
        return averagePercentage;
    }

    public void setAveragePercentage(BigDecimal averagePercentage) {
        this.averagePercentage = averagePercentage;
    }

    public BigDecimal getPassPercentage() {
        return passPercentage;
    }

    public void setPassPercentage(BigDecimal passPercentage) {
        this.passPercentage = passPercentage;
    }
}