package com.edusphere.dao;

import com.edusphere.util.DBConnection;
import com.edusphere.model.QuizQuestion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizQuestionDAO {

    public QuizQuestion createQuestion(QuizQuestion question)
            throws SQLException {

        String sql = """
                INSERT INTO quiz_questions
                    (quiz_id, question_text, marks, position)
                VALUES (?, ?, ?, ?)
                RETURNING id, quiz_id, question_text, marks, position
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, question.getQuizId());
            statement.setString(2, question.getQuestionText());
            statement.setInt(3, question.getMarks());
            statement.setInt(4, question.getPosition());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapQuestion(resultSet);
                }
            }
        }

        throw new SQLException("Failed to create quiz question.");
    }

    public QuizQuestion findById(Long id)
            throws SQLException {

        String sql = """
                SELECT id, quiz_id, question_text, marks, position
                FROM quiz_questions
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapQuestion(resultSet);
                }
            }
        }

        return null;
    }

    public List<QuizQuestion> findByQuiz(Long quizId)
            throws SQLException {

        String sql = """
                SELECT id, quiz_id, question_text, marks, position
                FROM quiz_questions
                WHERE quiz_id = ?
                ORDER BY position ASC
                """;

        List<QuizQuestion> questions = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, quizId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    questions.add(mapQuestion(resultSet));
                }
            }
        }

        return questions;
    }

    public QuizQuestion updateQuestion(QuizQuestion question)
            throws SQLException {

        String sql = """
                UPDATE quiz_questions
                SET question_text = ?,
                    marks = ?,
                    position = ?
                WHERE id = ?
                RETURNING id, quiz_id, question_text, marks, position
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, question.getQuestionText());
            statement.setInt(2, question.getMarks());
            statement.setInt(3, question.getPosition());
            statement.setLong(4, question.getId());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapQuestion(resultSet);
                }
            }
        }

        return null;
    }

    public boolean deleteQuestion(Long id)
            throws SQLException {

        String sql = """
                DELETE FROM quiz_questions
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        }
    }

    private QuizQuestion mapQuestion(ResultSet resultSet)
            throws SQLException {

        QuizQuestion question = new QuizQuestion();

        question.setId(
                resultSet.getLong("id")
        );

        question.setQuizId(
                resultSet.getLong("quiz_id")
        );

        question.setQuestionText(
                resultSet.getString("question_text")
        );

        question.setMarks(
                resultSet.getInt("marks")
        );

        question.setPosition(
                resultSet.getInt("position")
        );

        return question;
    }
}