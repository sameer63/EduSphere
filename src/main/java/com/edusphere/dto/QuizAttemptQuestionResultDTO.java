package com.edusphere.dto;

import java.math.BigDecimal;

public class QuizAttemptQuestionResultDTO {

    private Long questionId;
    private String questionText;

    private BigDecimal questionMarks;
    private BigDecimal marksAwarded;

    private Long selectedOptionId;
    private String selectedOptionText;

    private Long correctOptionId;
    private String correctOptionText;

    private boolean answered;
    private boolean correct;

    public QuizAttemptQuestionResultDTO() {
    }

    public QuizAttemptQuestionResultDTO(
            Long questionId,
            String questionText,
            BigDecimal questionMarks,
            BigDecimal marksAwarded,
            Long selectedOptionId,
            String selectedOptionText,
            Long correctOptionId,
            String correctOptionText,
            boolean answered,
            boolean correct
    ) {
        this.questionId = questionId;
        this.questionText = questionText;
        this.questionMarks = questionMarks;
        this.marksAwarded = marksAwarded;
        this.selectedOptionId = selectedOptionId;
        this.selectedOptionText = selectedOptionText;
        this.correctOptionId = correctOptionId;
        this.correctOptionText = correctOptionText;
        this.answered = answered;
        this.correct = correct;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public BigDecimal getQuestionMarks() {
        return questionMarks;
    }

    public void setQuestionMarks(BigDecimal questionMarks) {
        this.questionMarks = questionMarks;
    }

    public BigDecimal getMarksAwarded() {
        return marksAwarded;
    }

    public void setMarksAwarded(BigDecimal marksAwarded) {
        this.marksAwarded = marksAwarded;
    }

    public Long getSelectedOptionId() {
        return selectedOptionId;
    }

    public void setSelectedOptionId(Long selectedOptionId) {
        this.selectedOptionId = selectedOptionId;
    }

    public String getSelectedOptionText() {
        return selectedOptionText;
    }

    public void setSelectedOptionText(String selectedOptionText) {
        this.selectedOptionText = selectedOptionText;
    }

    public Long getCorrectOptionId() {
        return correctOptionId;
    }

    public void setCorrectOptionId(Long correctOptionId) {
        this.correctOptionId = correctOptionId;
    }

    public String getCorrectOptionText() {
        return correctOptionText;
    }

    public void setCorrectOptionText(String correctOptionText) {
        this.correctOptionText = correctOptionText;
    }

    public boolean isAnswered() {
        return answered;
    }

    public void setAnswered(boolean answered) {
        this.answered = answered;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}