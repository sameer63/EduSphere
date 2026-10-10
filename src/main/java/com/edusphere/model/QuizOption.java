package com.edusphere.model;

public class QuizOption {

    private Long id;
    private Long questionId;
    private String optionText;
    private boolean correct;

    public QuizOption() {
    }

    public QuizOption(Long questionId,
                      String optionText,
                      boolean correct) {

        this.questionId = questionId;
        this.optionText = optionText;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }
}