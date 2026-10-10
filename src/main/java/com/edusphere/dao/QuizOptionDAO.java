package com.edusphere.dao;

import com.edusphere.util.DBConnection;
import com.edusphere.model.QuizOption;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QuizOptionDAO {

    public QuizOption createOption(QuizOption option)
            throws SQLException {

        String sql = """
                INSERT INTO quiz_options
                    (question_id, option_text, is_correct)
                VALUES (?, ?, ?)
                RETURNING id, question_id, option_text, is_correct
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(
                    1,
                    option.getQuestionId()
            );

            statement.setString(
                    2,
                    option.getOptionText()
            );

            statement.setBoolean(
                    3,
                    option.isCorrect()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapOption(resultSet);
                }
            }
        }

        throw new SQLException(
                "Failed to create quiz option."
        );
    }

    public QuizOption findById(Long id)
            throws SQLException {

        String sql = """
                SELECT id,
                       question_id,
                       option_text,
                       is_correct
                FROM quiz_options
                WHERE id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapOption(resultSet);
                }
            }
        }

        return null;
    }

    public List<QuizOption> findByQuestion(
            Long questionId)
            throws SQLException {

        String sql = """
                SELECT id,
                       question_id,
                       option_text,
                       is_correct
                FROM quiz_options
                WHERE question_id = ?
                ORDER BY id ASC
                """;

        List<QuizOption> options =
                new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, questionId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    options.add(
                            mapOption(resultSet)
                    );
                }
            }
        }

        return options;
    }

    public QuizOption updateOption(
            QuizOption option)
            throws SQLException {

        String sql = """
                UPDATE quiz_options
                SET option_text = ?,
                    is_correct = ?
                WHERE id = ?
                RETURNING id,
                          question_id,
                          option_text,
                          is_correct
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    option.getOptionText()
            );

            statement.setBoolean(
                    2,
                    option.isCorrect()
            );

            statement.setLong(
                    3,
                    option.getId()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapOption(resultSet);
                }
            }
        }

        return null;
    }

    public boolean deleteOption(Long id)
            throws SQLException {

        String sql = """
                DELETE FROM quiz_options
                WHERE id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            return statement.executeUpdate() > 0;
        }
    }

    /*
     * Sets every option of a question to false.
     *
     * This will be useful when we mark one option
     * as the correct answer.
     */
    public void clearCorrectOptions(Long questionId)
            throws SQLException {

        String sql = """
                UPDATE quiz_options
                SET is_correct = FALSE
                WHERE question_id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, questionId);

            statement.executeUpdate();
        }
    }

    /*
     * Marks one particular option as correct.
     */
    public boolean markCorrect(Long optionId)
            throws SQLException {

        String sql = """
                UPDATE quiz_options
                SET is_correct = TRUE
                WHERE id = ?
                """;

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, optionId);

            return statement.executeUpdate() > 0;
        }
    }

    private QuizOption mapOption(
            ResultSet resultSet)
            throws SQLException {

        QuizOption option =
                new QuizOption();

        option.setId(
                resultSet.getLong("id")
        );

        option.setQuestionId(
                resultSet.getLong("question_id")
        );

        option.setOptionText(
                resultSet.getString("option_text")
        );

        option.setCorrect(
                resultSet.getBoolean("is_correct")
        );

        return option;
    }
}