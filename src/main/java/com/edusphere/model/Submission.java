package com.edusphere.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Submission {

    private Long id;
    private Long assignmentId;
    private Long studentId;
    private String submissionText;
    private LocalDateTime submittedAt;
    private BigDecimal marks;
    private String feedback;

    public Submission() {
    }

    public Submission(Long assignmentId,
                      Long studentId,
                      String submissionText) {

        this.assignmentId = assignmentId;
        this.studentId = studentId;
        this.submissionText = submissionText;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAssignmentId() {
        return assignmentId;
    }

    public void setAssignmentId(Long assignmentId) {
        this.assignmentId = assignmentId;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public String getSubmissionText() {
        return submissionText;
    }

    public void setSubmissionText(String submissionText) {
        this.submissionText = submissionText;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public BigDecimal getMarks() {
        return marks;
    }

    public void setMarks(BigDecimal marks) {
        this.marks = marks;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}