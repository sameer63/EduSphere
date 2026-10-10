package com.edusphere.dao;

import com.edusphere.util.DBConnection;
import com.edusphere.model.Submission;

import java.sql.*;

public class SubmissionDAO {

    public Submission createSubmission(Submission submission)
            throws SQLException {

        String sql = """
                INSERT INTO submissions
                    (assignment_id, student_id, submission_text)
                VALUES (?, ?, ?)
                RETURNING id,
                          assignment_id,
                          student_id,
                          submission_text,
                          submitted_at,
                          marks,
                          feedback
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, submission.getAssignmentId());
            statement.setLong(2, submission.getStudentId());

            if (submission.getSubmissionText() == null) {
                statement.setNull(3, Types.LONGVARCHAR);
            } else {
                statement.setString(3, submission.getSubmissionText());
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapSubmission(resultSet);
                }
            }
        }

        throw new SQLException("Failed to create submission.");
    }


    public Submission findById(Long id)
            throws SQLException {

        String sql = """
                SELECT id,
                       assignment_id,
                       student_id,
                       submission_text,
                       submitted_at,
                       marks,
                       feedback
                FROM submissions
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapSubmission(resultSet);
                }
            }
        }

        return null;
    }


    public Submission findByAssignmentAndStudent(
            Long assignmentId,
            Long studentId)
            throws SQLException {

        String sql = """
                SELECT id,
                       assignment_id,
                       student_id,
                       submission_text,
                       submitted_at,
                       marks,
                       feedback
                FROM submissions
                WHERE assignment_id = ?
                  AND student_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, assignmentId);
            statement.setLong(2, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapSubmission(resultSet);
                }
            }
        }

        return null;
    }


    public java.util.List<Submission> findByStudent(Long studentId)
            throws SQLException {

        String sql = """
                SELECT id,
                       assignment_id,
                       student_id,
                       submission_text,
                       submitted_at,
                       marks,
                       feedback
                FROM submissions
                WHERE student_id = ?
                ORDER BY submitted_at DESC
                """;

        java.util.List<Submission> submissions =
                new java.util.ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    submissions.add(mapSubmission(resultSet));
                }
            }
        }

        return submissions;
    }


    public java.util.List<Submission> findByAssignment(
            Long assignmentId)
            throws SQLException {

        String sql = """
                SELECT id,
                       assignment_id,
                       student_id,
                       submission_text,
                       submitted_at,
                       marks,
                       feedback
                FROM submissions
                WHERE assignment_id = ?
                ORDER BY submitted_at ASC
                """;

        java.util.List<Submission> submissions =
                new java.util.ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, assignmentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    submissions.add(mapSubmission(resultSet));
                }
            }
        }

        return submissions;
    }


    public Submission updateSubmission(Submission submission)
            throws SQLException {

        String sql = """
                UPDATE submissions
                SET submission_text = ?,
                    submitted_at = CURRENT_TIMESTAMP
                WHERE id = ?
                RETURNING id,
                          assignment_id,
                          student_id,
                          submission_text,
                          submitted_at,
                          marks,
                          feedback
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (submission.getSubmissionText() == null) {
                statement.setNull(1, Types.LONGVARCHAR);
            } else {
                statement.setString(1, submission.getSubmissionText());
            }

            statement.setLong(2, submission.getId());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapSubmission(resultSet);
                }
            }
        }

        return null;
    }


    public Submission gradeSubmission(
            Long submissionId,
            java.math.BigDecimal marks,
            String feedback)
            throws SQLException {

        String sql = """
                UPDATE submissions
                SET marks = ?,
                    feedback = ?
                WHERE id = ?
                RETURNING id,
                          assignment_id,
                          student_id,
                          submission_text,
                          submitted_at,
                          marks,
                          feedback
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (marks == null) {
                statement.setNull(1, Types.NUMERIC);
            } else {
                statement.setBigDecimal(1, marks);
            }

            if (feedback == null) {
                statement.setNull(2, Types.LONGVARCHAR);
            } else {
                statement.setString(2, feedback);
            }

            statement.setLong(3, submissionId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapSubmission(resultSet);
                }
            }
        }

        return null;
    }


    private Submission mapSubmission(ResultSet resultSet)
            throws SQLException {

        Submission submission = new Submission();

        submission.setId(
                resultSet.getLong("id")
        );

        submission.setAssignmentId(
                resultSet.getLong("assignment_id")
        );

        submission.setStudentId(
                resultSet.getLong("student_id")
        );

        submission.setSubmissionText(
                resultSet.getString("submission_text")
        );

        Timestamp submittedAt =
                resultSet.getTimestamp("submitted_at");

        if (submittedAt != null) {
            submission.setSubmittedAt(
                    submittedAt.toLocalDateTime()
            );
        }

        submission.setMarks(
                resultSet.getBigDecimal("marks")
        );

        submission.setFeedback(
                resultSet.getString("feedback")
        );

        return submission;
    }
}