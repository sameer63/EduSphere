package com.edusphere.dao;

import com.edusphere.util.DBConnection;
import com.edusphere.model.Quiz;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizDAO {

    public Quiz createQuiz(Quiz quiz) throws SQLException {

        String sql = """
                INSERT INTO quizzes
                    (course_id, title, description, max_attempts)
                VALUES (?, ?, ?, ?)
                RETURNING id,
                          course_id,
                          title,
                          description,
                          max_attempts,
                          created_at
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, quiz.getCourseId());
            statement.setString(2, quiz.getTitle());

            if (quiz.getDescription() == null) {
                statement.setNull(3, Types.LONGVARCHAR);
            } else {
                statement.setString(3, quiz.getDescription());
            }

            if (quiz.getMaxAttempts() == null) {
                statement.setNull(4, Types.INTEGER);
            } else {
                statement.setInt(4, quiz.getMaxAttempts());
            }

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapQuiz(resultSet);
                }
            }
        }

        throw new SQLException("Failed to create quiz.");
    }


    public Quiz findById(Long id) throws SQLException {

        String sql = """
                SELECT id,
                       course_id,
                       title,
                       description,
                       max_attempts,
                       created_at
                FROM quizzes
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapQuiz(resultSet);
                }
            }
        }

        return null;
    }


    public List<Quiz> findByCourse(Long courseId)
            throws SQLException {

        String sql = """
                SELECT id,
                       course_id,
                       title,
                       description,
                       max_attempts,
                       created_at
                FROM quizzes
                WHERE course_id = ?
                ORDER BY created_at DESC
                """;

        List<Quiz> quizzes = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, courseId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    quizzes.add(mapQuiz(resultSet));
                }
            }
        }

        return quizzes;
    }


    public Quiz updateQuiz(Quiz quiz) throws SQLException {

        String sql = """
                UPDATE quizzes
                SET title = ?,
                    description = ?,
                    max_attempts = ?
                WHERE id = ?
                RETURNING id,
                          course_id,
                          title,
                          description,
                          max_attempts,
                          created_at
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, quiz.getTitle());

            if (quiz.getDescription() == null) {
                statement.setNull(2, Types.LONGVARCHAR);
            } else {
                statement.setString(2, quiz.getDescription());
            }

            if (quiz.getMaxAttempts() == null) {
                statement.setNull(3, Types.INTEGER);
            } else {
                statement.setInt(3, quiz.getMaxAttempts());
            }

            statement.setLong(4, quiz.getId());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapQuiz(resultSet);
                }
            }
        }

        return null;
    }


    public boolean deleteQuiz(Long id) throws SQLException {

        String sql = """
                DELETE FROM quizzes
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        }
    }


    private Quiz mapQuiz(ResultSet resultSet)
            throws SQLException {

        Quiz quiz = new Quiz();

        quiz.setId(
                resultSet.getLong("id")
        );

        quiz.setCourseId(
                resultSet.getLong("course_id")
        );

        quiz.setTitle(
                resultSet.getString("title")
        );

        quiz.setDescription(
                resultSet.getString("description")
        );

        int maxAttempts =
                resultSet.getInt("max_attempts");

        if (resultSet.wasNull()) {
            quiz.setMaxAttempts(null);
        } else {
            quiz.setMaxAttempts(maxAttempts);
        }

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            quiz.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return quiz;
    }
}