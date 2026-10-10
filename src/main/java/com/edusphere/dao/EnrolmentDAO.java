package com.edusphere.dao;

import com.edusphere.model.Enrolment;
import com.edusphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class EnrolmentDAO {

    private static final String INSERT_ENROLMENT = """
            INSERT INTO enrolments
                (student_id, course_id, status)
            VALUES
                (?, ?, ?)
            RETURNING id, enrolled_at
            """;

    private static final String FIND_BY_ID = """
            SELECT id,
                   student_id,
                   course_id,
                   status,
                   enrolled_at,
                   completed_at
            FROM enrolments
            WHERE id = ?
            """;

    private static final String FIND_BY_STUDENT_AND_COURSE = """
            SELECT id,
                   student_id,
                   course_id,
                   status,
                   enrolled_at,
                   completed_at
            FROM enrolments
            WHERE student_id = ?
              AND course_id = ?
            """;

    private static final String FIND_BY_STUDENT = """
            SELECT id,
                   student_id,
                   course_id,
                   status,
                   enrolled_at,
                   completed_at
            FROM enrolments
            WHERE student_id = ?
            ORDER BY enrolled_at DESC
            """;

    private static final String FIND_BY_COURSE = """
            SELECT id,
                   student_id,
                   course_id,
                   status,
                   enrolled_at,
                   completed_at
            FROM enrolments
            WHERE course_id = ?
            ORDER BY enrolled_at DESC
            """;

    public Enrolment createEnrolment(
            Connection connection,
            Enrolment enrolment
    ) throws Exception {

        String sql = """
            INSERT INTO enrolments (
                student_id,
                course_id,
                status
            )
            VALUES (?, ?, ?)
            RETURNING
                id,
                student_id,
                course_id,
                status,
                enrolled_at,
                completed_at
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    enrolment.getStudentId()
            );

            statement.setLong(
                    2,
                    enrolment.getCourseId()
            );

            statement.setString(
                    3,
                    enrolment.getStatus()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return mapEnrolment(resultSet);
                }
            }
        }

        return null;
    }
    public Enrolment createEnrolment(
            Enrolment enrolment) {

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_ENROLMENT)) {

            statement.setLong(
                    1,
                    enrolment.getStudentId()
            );

            statement.setLong(
                    2,
                    enrolment.getCourseId()
            );

            statement.setString(
                    3,
                    enrolment.getStatus()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    enrolment.setId(
                            resultSet.getLong("id")
                    );

                    enrolment.setEnrolledAt(
                            resultSet
                                    .getTimestamp("enrolled_at")
                                    .toLocalDateTime()
                    );

                    return enrolment;
                }

                throw new RuntimeException(
                        "Failed to create enrolment."
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error creating enrolment.",
                    e
            );
        }
    }

    public Enrolment cancelEnrolment(
            Connection connection,
            Long studentId,
            Long courseId
    ) throws Exception {

        String sql = """
            UPDATE enrolments
            SET status = 'CANCELLED',
                completed_at = NULL
            WHERE student_id = ?
              AND course_id = ?
              AND status = 'ACTIVE'
            RETURNING
                id,
                student_id,
                course_id,
                status,
                enrolled_at,
                completed_at
            """;

        try (PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, studentId);
            statement.setLong(2, courseId);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapEnrolment(resultSet);
                }
            }
        }

        return null;
    }

    public Optional<Enrolment> findById(Long id) {

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(FIND_BY_ID)) {

            statement.setLong(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapEnrolment(resultSet)
                    );
                }

                return Optional.empty();
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error finding enrolment.",
                    e
            );
        }
    }

    public Optional<Enrolment>
    findByStudentAndCourse(
            Long studentId,
            Long courseId) {

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_STUDENT_AND_COURSE)) {

            statement.setLong(1, studentId);
            statement.setLong(2, courseId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(
                            mapEnrolment(resultSet)
                    );
                }

                return Optional.empty();
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error finding enrolment.",
                    e
            );
        }
    }

    public List<Enrolment>
    findByStudent(Long studentId) {

        List<Enrolment> enrolments =
                new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_STUDENT)) {

            statement.setLong(1, studentId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    enrolments.add(
                            mapEnrolment(resultSet)
                    );
                }
            }

            return enrolments;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error finding student enrolments.",
                    e
            );
        }
    }

    public List<Enrolment>
    findByCourse(Long courseId) {

        List<Enrolment> enrolments =
                new ArrayList<>();

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_COURSE)) {

            statement.setLong(1, courseId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    enrolments.add(
                            mapEnrolment(resultSet)
                    );
                }
            }

            return enrolments;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error finding course enrolments.",
                    e
            );
        }
    }

    private Enrolment mapEnrolment(
            ResultSet resultSet)
            throws SQLException {

        Enrolment enrolment =
                new Enrolment();

        enrolment.setId(
                resultSet.getLong("id")
        );

        enrolment.setStudentId(
                resultSet.getLong("student_id")
        );

        enrolment.setCourseId(
                resultSet.getLong("course_id")
        );

        enrolment.setStatus(
                resultSet.getString("status")
        );

        if (resultSet.getTimestamp("enrolled_at")
                != null) {

            enrolment.setEnrolledAt(
                    resultSet
                            .getTimestamp("enrolled_at")
                            .toLocalDateTime()
            );
        }

        if (resultSet.getTimestamp("completed_at")
                != null) {

            enrolment.setCompletedAt(
                    resultSet
                            .getTimestamp("completed_at")
                            .toLocalDateTime()
            );
        }

        return enrolment;
    }
}