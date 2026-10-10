package com.edusphere.dao;

import com.edusphere.model.Course;
import com.edusphere.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDAO {

    private static final String INSERT_COURSE = """
            INSERT INTO courses
                (
                    teacher_id,
                    category_id,
                    title,
                    description,
                    price,
                    status
                )
            VALUES
                (?, ?, ?, ?, ?, ?)
            RETURNING
                id,
                created_at,
                updated_at
            """;

    private static final String FIND_BY_ID = """
            SELECT
                id,
                teacher_id,
                category_id,
                title,
                description,
                price,
                status,
                created_at,
                updated_at
            FROM courses
            WHERE id = ?
            """;

    private static final String FIND_BY_TEACHER_ID = """
            SELECT
                id,
                teacher_id,
                category_id,
                title,
                description,
                price,
                status,
                created_at,
                updated_at
            FROM courses
            WHERE teacher_id = ?
            ORDER BY created_at DESC
            """;

    private static final String FIND_PUBLISHED = """
            SELECT
                id,
                teacher_id,
                category_id,
                title,
                description,
                price,
                status,
                created_at,
                updated_at
            FROM courses
            WHERE status = 'PUBLISHED'
            ORDER BY created_at DESC
            """;

    private static final String UPDATE_COURSE = """
        UPDATE courses
        SET category_id = ?,
            title = ?,
            description = ?,
            price = ?,
            status = ?,
            updated_at = CURRENT_TIMESTAMP
        WHERE id = ?
        RETURNING id, teacher_id, category_id, title,
                  description, price, status,
                  created_at, updated_at
        """;

    public Course createCourse(Course course) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(INSERT_COURSE)) {
            statement.setLong(1, course.getTeacherId());

            statement.setLong(2, course.getCategoryId());

            statement.setString(3, course.getTitle());

            statement.setString(4, course.getDescription());

            statement.setBigDecimal(5, course.getPrice());

            statement.setString(6, course.getStatus());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    course.setId(resultSet.getLong("id"));
                    Timestamp createdAt =
                            resultSet.getTimestamp("created_at");
                    if (createdAt != null) {
                        course.setCreatedAt(createdAt.toLocalDateTime());
                    }
                    Timestamp updatedAt =
                            resultSet.getTimestamp("updated_at");
                    if (updatedAt != null) {
                        course.setUpdatedAt(updatedAt.toLocalDateTime());
                    }
                }
            }
        }

        return course;
    }

    public Optional<Course> findById(Long id) throws SQLException {

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(FIND_BY_ID)){

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapCourse(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public List<Course> findByTeacherId(Long teacherId) throws SQLException {

        List<Course> courses = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_TEACHER_ID)){
            statement.setLong(1, teacherId);
            try (ResultSet resultSet = statement.executeQuery()){
                while (resultSet.next()) {
                    courses.add(mapCourse(resultSet));
                }
            }
        }

        return courses;
    }

    public List<Course> findAllPublished() throws SQLException {

        List<Course> courses = new ArrayList<>();

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_PUBLISHED);
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                courses.add(mapCourse(resultSet));
            }
        }
        return courses;
    }

    public Course updateCourse(Course course) {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(UPDATE_COURSE)) {

            statement.setLong(1, course.getCategoryId());
            statement.setString(2, course.getTitle());
            statement.setString(3, course.getDescription());
            statement.setBigDecimal(4, course.getPrice());
            statement.setString(5, course.getStatus());
            statement.setLong(6, course.getId());

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return mapCourse(resultSet);
                }

                throw new IllegalArgumentException(
                        "Course not found."
                );
            }

        } catch (SQLException e) {

            throw new RuntimeException(
                    "Error updating course.",
                    e
            );
        }
    }

    private Course mapCourse(
            ResultSet resultSet
    ) throws SQLException {

        Course course =
                new Course();

        course.setId(
                resultSet.getLong("id")
        );

        course.setTeacherId(
                resultSet.getLong("teacher_id")
        );

        course.setCategoryId(
                resultSet.getLong("category_id")
        );

        course.setTitle(
                resultSet.getString("title")
        );

        course.setDescription(
                resultSet.getString("description")
        );

        course.setPrice(
                resultSet.getBigDecimal("price")
        );

        course.setStatus(
                resultSet.getString("status")
        );

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {

            course.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        Timestamp updatedAt =
                resultSet.getTimestamp("updated_at");

        if (updatedAt != null) {

            course.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }

        return course;
    }
}