package com.edusphere.model;

public class QuizQuestion {

    private Long id;
    private Long quizId;
    private String questionText;
    private Integer marks;
    private Integer position;

    public QuizQuestion() {
    }

    public QuizQuestion(Long quizId,
                        String questionText,
                        Integer marks,
                        Integer position) {

        this.quizId = quizId;
        this.questionText = questionText;
        this.marks = marks;
        this.position = position;
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

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Integer getMarks() {
        return marks;
    }

    public void setMarks(Integer marks) {
        this.marks = marks;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}