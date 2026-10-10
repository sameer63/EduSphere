package com.edusphere.dao;

import com.edusphere.util.DBConnection;
import com.edusphere.model.QuizAttemptAnswer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizAttemptAnswerDAO {

    public QuizAttemptAnswer createAnswer(
            QuizAttemptAnswer answer) throws SQLException {

        String sql = """
                INSERT INTO quiz_attempt_answers
                    (attempt_id, question_id, option_id)
                VALUES (?, ?, ?)
                RETURNING id, attempt_id, question_id,
                          option_id, created_at
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, answer.getAttemptId());
            statement.setLong(2, answer.getQuestionId());
            statement.setLong(3, answer.getOptionId());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapAnswer(resultSet);
                }
            }
        }

        throw new SQLException("Failed to create quiz attempt answer.");
    }

    public Optional<QuizAttemptAnswer> findById(Long id)
            throws SQLException {

        String sql = """
                SELECT id, attempt_id, question_id,
                       option_id, created_at
                FROM quiz_attempt_answers
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapAnswer(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public Optional<QuizAttemptAnswer> findByAttemptAndQuestion(
            Long attemptId,
            Long questionId) throws SQLException {

        String sql = """
                SELECT id, attempt_id, question_id,
                       option_id, created_at
                FROM quiz_attempt_answers
                WHERE attempt_id = ?
                  AND question_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, attemptId);
            statement.setLong(2, questionId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapAnswer(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<QuizAttemptAnswer> findByAttempt(
            Long attemptId) throws SQLException {

        String sql = """
                SELECT id, attempt_id, question_id,
                       option_id, created_at
                FROM quiz_attempt_answers
                WHERE attempt_id = ?
                ORDER BY question_id ASC
                """;

        List<QuizAttemptAnswer> answers = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, attemptId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    answers.add(mapAnswer(resultSet));
                }
            }
        }

        return answers;
    }

    public boolean updateAnswer(
            Long attemptId,
            Long questionId,
            Long optionId) throws SQLException {

        String sql = """
                UPDATE quiz_attempt_answers
                SET option_id = ?
                WHERE attempt_id = ?
                  AND question_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, optionId);
            statement.setLong(2, attemptId);
            statement.setLong(3, questionId);

            return statement.executeUpdate() > 0;
        }
    }

    public boolean deleteAnswer(
            Long attemptId,
            Long questionId) throws SQLException {

        String sql = """
                DELETE FROM quiz_attempt_answers
                WHERE attempt_id = ?
                  AND question_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, attemptId);
            statement.setLong(2, questionId);

            return statement.executeUpdate() > 0;
        }
    }

    private QuizAttemptAnswer mapAnswer(
            ResultSet resultSet) throws SQLException {

        QuizAttemptAnswer answer =
                new QuizAttemptAnswer();

        answer.setId(
                resultSet.getLong("id")
        );

        answer.setAttemptId(
                resultSet.getLong("attempt_id")
        );

        answer.setQuestionId(
                resultSet.getLong("question_id")
        );

        answer.setOptionId(
                resultSet.getLong("option_id")
        );

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            answer.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return answer;
    }
}