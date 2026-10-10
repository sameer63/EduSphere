package com.edusphere.dao;

import com.edusphere.model.Lesson;
import com.edusphere.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LessonDAO {

    private static final String INSERT_LESSON = """
            INSERT INTO lessons
                (course_id, title, content, position)
            VALUES
                (?, ?, ?, ?)
            RETURNING id, created_at, updated_at
            """;

    private static final String FIND_BY_ID = """
            SELECT id,
                   course_id,
                   title,
                   content,
                   position,
                   created_at,
                   updated_at
            FROM lessons
            WHERE id = ?
            """;

    private static final String FIND_BY_COURSE = """
            SELECT id,
                   course_id,
                   title,
                   content,
                   position,
                   created_at,
                   updated_at
            FROM lessons
            WHERE course_id = ?
            ORDER BY position ASC
            """;

    public Lesson createLesson(
            Lesson lesson) {

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             INSERT_LESSON)) {

            statement.setLong(
                    1,
                    lesson.getCourseId()
            );

            statement.setString(
                    2,
                    lesson.getTitle()
            );

            statement.setString(
                    3,
                    lesson.getContent()
            );

            statement.setInt(
                    4,
                    lesson.getPosition()
            );

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    lesson.setId(
                            resultSet.getLong("id")
                    );

                    if (resultSet.getTimestamp(
                            "created_at") != null) {

                        lesson.setCreatedAt(
                                resultSet
                                        .getTimestamp(
                                                "created_at")
                                        .toLocalDateTime()
                        );
                    }

                    if (resultSet.getTimestamp(
                            "updated_at") != null) {

                        lesson.setUpdatedAt(
                                resultSet
                                        .getTimestamp(
                                                "updated_at")
                                        .toLocalDateTime()
                        );
                    }

                    return lesson;
                }

                throw new RuntimeException(
                        "Failed to create lesson."
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error creating lesson.",
                    e
            );
        }
    }

    public Optional<Lesson> findById(
            Long id) {

        try (Connection connection =
                     DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(
                             FIND_BY_ID)) {

            statement.setLong(1, id);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {

                    return Optional.of(
                            mapLesson(resultSet)
                    );
                }

                return Optional.empty();
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error finding lesson.",
                    e
            );
        }
    }

    public List<Lesson> findByCourse(
            Long courseId) {

        List<Lesson> lessons =
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

                    lessons.add(
                            mapLesson(resultSet)
                    );
                }
            }

            return lessons;

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error finding course lessons.",
                    e
            );
        }
    }

    public Lesson updateLesson(Lesson lesson) throws Exception {
        String sql = """
            UPDATE lessons
            SET title = ?,
                content = ?,
                position = ?,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
            RETURNING id, course_id, title, content,
                      position, created_at, updated_at
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, lesson.getTitle());
            statement.setString(2, lesson.getContent());
            statement.setInt(3, lesson.getPosition());
            statement.setLong(4, lesson.getId());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapLesson(resultSet);
                }
                return null;
            }
        }
    }

    public boolean deleteLesson(Long lessonId) throws Exception {

        String sql = """
            DELETE FROM lessons
            WHERE id = ?
            """;

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setLong(1, lessonId);

            int rowsAffected = statement.executeUpdate();

            return rowsAffected > 0;
        }
    }
    private Lesson mapLesson(
            ResultSet resultSet)
            throws SQLException {

        Lesson lesson =
                new Lesson();

        lesson.setId(
                resultSet.getLong("id")
        );

        lesson.setCourseId(
                resultSet.getLong("course_id")
        );

        lesson.setTitle(
                resultSet.getString("title")
        );

        lesson.setContent(
                resultSet.getString("content")
        );

        lesson.setPosition(
                resultSet.getInt("position")
        );

        if (resultSet.getTimestamp(
                "created_at") != null) {

            lesson.setCreatedAt(
                    resultSet
                            .getTimestamp("created_at")
                            .toLocalDateTime()
            );
        }

        if (resultSet.getTimestamp(
                "updated_at") != null) {

            lesson.setUpdatedAt(
                    resultSet
                            .getTimestamp("updated_at")
                            .toLocalDateTime()
            );
        }

        return lesson;
    }
}