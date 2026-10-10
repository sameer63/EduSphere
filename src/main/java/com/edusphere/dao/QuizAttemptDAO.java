package com.edusphere.dao;

import com.edusphere.dto.QuizAnalyticsDTO;
import com.edusphere.dto.QuizAttemptSummaryDTO;
import com.edusphere.util.DBConnection;
import com.edusphere.model.QuizAttempt;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class QuizAttemptDAO {

    public QuizAttempt createAttempt(QuizAttempt attempt)
            throws SQLException {

        String sql = """
                INSERT INTO quiz_attempts
                    (quiz_id, student_id, attempt_number, started_at)
                VALUES (?, ?, ?, CURRENT_TIMESTAMP)
                RETURNING id, quiz_id, student_id, attempt_number,
                          score, started_at, submitted_at
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, attempt.getQuizId());
            statement.setLong(2, attempt.getStudentId());
            statement.setInt(3, attempt.getAttemptNumber());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapAttempt(resultSet);
                }
            }
        }

        throw new SQLException("Failed to create quiz attempt.");
    }

    public Optional<QuizAttempt> findById(Long id)
            throws SQLException {

        String sql = """
                SELECT id, quiz_id, student_id, attempt_number,
                       score, started_at, submitted_at
                FROM quiz_attempts
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapAttempt(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public List<QuizAttempt> findByQuizAndStudent(
            Long quizId,
            Long studentId) throws SQLException {

        String sql = """
                SELECT id, quiz_id, student_id, attempt_number,
                       score, started_at, submitted_at
                FROM quiz_attempts
                WHERE quiz_id = ?
                  AND student_id = ?
                ORDER BY attempt_number ASC
                """;

        List<QuizAttempt> attempts = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, quizId);
            statement.setLong(2, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    attempts.add(mapAttempt(resultSet));
                }
            }
        }

        return attempts;
    }

    public Optional<QuizAttempt> findActiveAttempt(
            Long quizId,
            Long studentId) throws SQLException {

        String sql = """
                SELECT id, quiz_id, student_id, attempt_number,
                       score, started_at, submitted_at
                FROM quiz_attempts
                WHERE quiz_id = ?
                  AND student_id = ?
                  AND submitted_at IS NULL
                ORDER BY attempt_number DESC
                LIMIT 1
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, quizId);
            statement.setLong(2, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapAttempt(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    public int getAttemptCount(
            Long quizId,
            Long studentId) throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM quiz_attempts
                WHERE quiz_id = ?
                  AND student_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, quizId);
            statement.setLong(2, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }

    public int getNextAttemptNumber(
            Long quizId,
            Long studentId) throws SQLException {

        String sql = """
                SELECT COALESCE(MAX(attempt_number), 0) + 1
                FROM quiz_attempts
                WHERE quiz_id = ?
                  AND student_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, quizId);
            statement.setLong(2, studentId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 1;
    }

    public boolean submitAttempt(
            Long attemptId,
            java.math.BigDecimal score) throws SQLException {

        String sql = """
                UPDATE quiz_attempts
                SET score = ?,
                    submitted_at = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND submitted_at IS NULL
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setBigDecimal(1, score);
            statement.setLong(2, attemptId);

            return statement.executeUpdate() > 0;
        }
    }

    public List<QuizAttempt> findByQuiz(Long quizId)
            throws SQLException {

        String sql = """
                SELECT id, quiz_id, student_id, attempt_number,
                       score, started_at, submitted_at
                FROM quiz_attempts
                WHERE quiz_id = ?
                ORDER BY started_at DESC
                """;

        List<QuizAttempt> attempts = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, quizId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {
                    attempts.add(mapAttempt(resultSet));
                }
            }
        }

        return attempts;
    }

    public QuizAnalyticsDTO getQuizAnalytics(
            Long quizId,
            BigDecimal totalMarks
    ) throws Exception {

        String sql = """
            SELECT
                COUNT(*) AS total_attempts,

                COUNT(
                    CASE
                        WHEN submitted_at IS NOT NULL
                        THEN 1
                    END
                ) AS submitted_attempts,

                COUNT(
                    CASE
                        WHEN submitted_at IS NULL
                        THEN 1
                    END
                ) AS in_progress_attempts,

                AVG(
                    CASE
                        WHEN submitted_at IS NOT NULL
                        THEN score
                    END
                ) AS average_score,

                MAX(
                    CASE
                        WHEN submitted_at IS NOT NULL
                        THEN score
                    END
                ) AS highest_score,

                MIN(
                    CASE
                        WHEN submitted_at IS NOT NULL
                        THEN score
                    END
                ) AS lowest_score,

                AVG(
                    CASE
                        WHEN submitted_at IS NOT NULL
                             AND ? > 0
                        THEN (score * 100.0) / ?
                    END
                ) AS average_percentage,

                AVG(
                    CASE
                        WHEN submitted_at IS NOT NULL
                             AND ? > 0
                             AND ((score * 100.0) / ?) >= 40
                        THEN 100.0
                        WHEN submitted_at IS NOT NULL
                             AND ? > 0
                        THEN 0.0
                    END
                ) AS pass_percentage

            FROM quiz_attempts
            WHERE quiz_id = ?
            """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            /*
             * Parameters for average percentage
             */
            statement.setBigDecimal(1, totalMarks);
            statement.setBigDecimal(2, totalMarks);

            /*
             * Parameters for pass percentage
             */
            statement.setBigDecimal(3, totalMarks);
            statement.setBigDecimal(4, totalMarks);
            statement.setBigDecimal(5, totalMarks);

            /*
             * quiz_id
             */
            statement.setLong(6, quizId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    long totalAttempts =
                            resultSet.getLong("total_attempts");

                    long submittedAttempts =
                            resultSet.getLong("submitted_attempts");

                    long inProgressAttempts =
                            resultSet.getLong("in_progress_attempts");

                    BigDecimal averageScore =
                            resultSet.getBigDecimal(
                                    "average_score"
                            );

                    BigDecimal highestScore =
                            resultSet.getBigDecimal(
                                    "highest_score"
                            );

                    BigDecimal lowestScore =
                            resultSet.getBigDecimal(
                                    "lowest_score"
                            );

                    BigDecimal averagePercentage =
                            resultSet.getBigDecimal(
                                    "average_percentage"
                            );

                    BigDecimal passPercentage =
                            resultSet.getBigDecimal(
                                    "pass_percentage"
                            );

                    return new QuizAnalyticsDTO(
                            quizId,
                            totalAttempts,
                            submittedAttempts,
                            inProgressAttempts,
                            averageScore,
                            highestScore,
                            lowestScore,
                            averagePercentage,
                            passPercentage
                    );
                }
            }
        }

        return new QuizAnalyticsDTO(
                quizId,
                0,
                0,
                0,
                null,
                null,
                null,
                null,
                null
        );
    }

    public List<QuizAttemptSummaryDTO> findByQuizWithStudentDetails(
            Long quizId
    ) throws Exception {

        String sql = """
            SELECT
                qa.id,
                qa.quiz_id,
                qa.student_id,
                u.name AS student_name,
                u.email AS student_email,
                qa.attempt_number,
                qa.score,
                qa.started_at,
                qa.submitted_at
            FROM quiz_attempts qa
            JOIN students s
                ON qa.student_id = s.id
            JOIN users u
                ON s.user_id = u.id
            WHERE qa.quiz_id = ?
            ORDER BY qa.started_at DESC
            """;

        List<QuizAttemptSummaryDTO> attempts = new ArrayList<>();

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)
        ) {

            statement.setLong(1, quizId);

            try (ResultSet resultSet = statement.executeQuery()) {

                while (resultSet.next()) {

                    QuizAttemptSummaryDTO dto =
                            new QuizAttemptSummaryDTO(
                                    resultSet.getLong("id"),
                                    resultSet.getLong("quiz_id"),
                                    resultSet.getLong("student_id"),
                                    resultSet.getString("student_name"),
                                    resultSet.getString("student_email"),
                                    resultSet.getInt("attempt_number"),
                                    resultSet.getBigDecimal("score"),
                                    resultSet.getTimestamp("started_at")
                                            .toLocalDateTime(),
                                    resultSet.getTimestamp("submitted_at") != null
                                            ? resultSet.getTimestamp("submitted_at")
                                            .toLocalDateTime()
                                            : null
                            );

                    attempts.add(dto);
                }
            }
        }

        return attempts;
    }
    private QuizAttempt mapAttempt(ResultSet resultSet)
            throws SQLException {

        QuizAttempt attempt = new QuizAttempt();

        attempt.setId(resultSet.getLong("id"));
        attempt.setQuizId(resultSet.getLong("quiz_id"));
        attempt.setStudentId(resultSet.getLong("student_id"));
        attempt.setAttemptNumber(
                resultSet.getInt("attempt_number")
        );

        attempt.setScore(
                resultSet.getBigDecimal("score")
        );

        Timestamp startedAt =
                resultSet.getTimestamp("started_at");

        if (startedAt != null) {
            attempt.setStartedAt(
                    startedAt.toLocalDateTime()
            );
        }

        Timestamp submittedAt =
                resultSet.getTimestamp("submitted_at");

        if (submittedAt != null) {
            attempt.setSubmittedAt(
                    submittedAt.toLocalDateTime()
            );
        }

        return attempt;
    }
}