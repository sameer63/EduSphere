package com.edusphere.dao;

import com.edusphere.util.DBConnection;
import com.edusphere.model.Assignment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class AssignmentDAO {

    public Assignment createAssignment(Assignment assignment)
            throws Exception {

        String sql = """
                INSERT INTO assignments
                    (course_id, title, description, deadline, max_marks)
                VALUES
                    (?, ?, ?, ?, ?)
                RETURNING id, course_id, title, description,
                          deadline, max_marks, created_at
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, assignment.getCourseId());
            statement.setString(2, assignment.getTitle());
            statement.setString(3, assignment.getDescription());

            if (assignment.getDeadline() != null) {
                statement.setTimestamp(
                        4,
                        Timestamp.valueOf(assignment.getDeadline())
                );
            } else {
                statement.setTimestamp(4, null);
            }

            statement.setBigDecimal(
                    5,
                    assignment.getMaxMarks()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapAssignment(resultSet);
                }

                return null;
            }
        }
    }

    public Assignment findById(Long id) throws Exception {

        String sql = """
                SELECT id, course_id, title, description,
                       deadline, max_marks, created_at
                FROM assignments
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapAssignment(resultSet);
                }

                return null;
            }
        }
    }

    public List<Assignment> findByCourse(Long courseId)
            throws Exception {

        String sql = """
                SELECT id, course_id, title, description,
                       deadline, max_marks, created_at
                FROM assignments
                WHERE course_id = ?
                ORDER BY deadline ASC NULLS LAST,
                         created_at DESC
                """;

        List<Assignment> assignments =
                new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, courseId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    assignments.add(
                            mapAssignment(resultSet)
                    );
                }
            }
        }

        return assignments;
    }

    public Assignment updateAssignment(
            Assignment assignment)
            throws Exception {

        String sql = """
                UPDATE assignments
                SET title = ?,
                    description = ?,
                    deadline = ?,
                    max_marks = ?
                WHERE id = ?
                RETURNING id, course_id, title, description,
                          deadline, max_marks, created_at
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(
                    1,
                    assignment.getTitle()
            );

            statement.setString(
                    2,
                    assignment.getDescription()
            );

            if (assignment.getDeadline() != null) {
                statement.setTimestamp(
                        3,
                        Timestamp.valueOf(
                                assignment.getDeadline()
                        )
                );
            } else {
                statement.setTimestamp(3, null);
            }

            statement.setBigDecimal(
                    4,
                    assignment.getMaxMarks()
            );

            statement.setLong(
                    5,
                    assignment.getId()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapAssignment(resultSet);
                }

                return null;
            }
        }
    }

    public boolean deleteAssignment(Long id)
            throws Exception {

        String sql = """
                DELETE FROM assignments
                WHERE id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setLong(1, id);

            int rowsAffected =
                    statement.executeUpdate();

            return rowsAffected > 0;
        }
    }

    private Assignment mapAssignment(
            ResultSet resultSet)
            throws Exception {

        Assignment assignment =
                new Assignment();

        assignment.setId(
                resultSet.getLong("id")
        );

        assignment.setCourseId(
                resultSet.getLong("course_id")
        );

        assignment.setTitle(
                resultSet.getString("title")
        );

        assignment.setDescription(
                resultSet.getString("description")
        );

        Timestamp deadline =
                resultSet.getTimestamp("deadline");

        if (deadline != null) {
            assignment.setDeadline(
                    deadline.toLocalDateTime()
            );
        }

        assignment.setMaxMarks(
                resultSet.getBigDecimal("max_marks")
        );

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            assignment.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        return assignment;
    }
}