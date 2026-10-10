package com.edusphere.model;

import java.time.LocalDateTime;

public class QuizAttemptAnswer {

    private Long id;
    private Long attemptId;
    private Long questionId;
    private Long optionId;
    private LocalDateTime createdAt;

    public QuizAttemptAnswer() {
    }

    public QuizAttemptAnswer(Long attemptId,
                             Long questionId,
                             Long optionId) {

        this.attemptId = attemptId;
        this.questionId = questionId;
        this.optionId = optionId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAttemptId() {
        return attemptId;
    }

    public void setAttemptId(Long attemptId) {
        this.attemptId = attemptId;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getOptionId() {
        return optionId;
    }

    public void setOptionId(Long optionId) {
        this.optionId = optionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}