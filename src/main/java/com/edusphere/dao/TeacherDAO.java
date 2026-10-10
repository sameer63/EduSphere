package com.edusphere.dao;

import com.edusphere.model.Teacher;
import com.edusphere.util.DBConnection;

import java.sql.*;
import java.util.Optional;

public class TeacherDAO {
    private static final String INSERT_TEACHER = """
            INSERT INTO teachers
                (user_id, qualification, bio)
            VALUES
                (?, ?, ?)
            RETURNING id, created_at
            """;

    private static final String FIND_BY_ID = """
            SELECT
                id,
                user_id,
                qualification,
                bio,
                created_at
            FROM teachers
            WHERE id = ?
            """;

    private static final String FIND_BY_USER_ID = """
            SELECT
                id,
                user_id,
                qualification,
                bio,
                created_at
            FROM teachers
            WHERE user_id = ?
            """;

    public Teacher createTeacher(Teacher teacher)throws SQLException{
        try(Connection connection= DBConnection.getConnection();
            PreparedStatement statement=connection.prepareStatement(INSERT_TEACHER)){
            statement.setLong(1,teacher.getUserId());
            statement.setString(2,teacher.getQualification());
            statement.setString(3,teacher.getBio());

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    teacher.setId(resultSet.getLong("id"));
                   Timestamp createdAt=resultSet.getTimestamp("created_at");
                   if(createdAt!=null){
                       teacher.setCreatedAt(createdAt.toLocalDateTime());
                   }
                }
            }
        }
        return teacher;
    }
    public Optional<Teacher> findById(Long id)throws SQLException{
        try(Connection connection=DBConnection.getConnection();
            PreparedStatement statement=connection.prepareStatement(FIND_BY_ID)){
            statement.setLong(1,id);

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return Optional.of(mapTeacher(resultSet));
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Teacher> findByUserId(Long userId)
            throws SQLException {

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_USER_ID)){

            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(
                            mapTeacher(resultSet)
                    );
                }
            }
        }
        return Optional.empty();
    }

    private Teacher mapTeacher(ResultSet resultSet)
            throws SQLException {

        Teacher teacher = new Teacher();

        teacher.setId(resultSet.getLong("id"));
        teacher.setUserId(resultSet.getLong("user_id"));
        teacher.setQualification(resultSet.getString("qualification"));
        teacher.setBio(resultSet.getString("bio"));
        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            teacher.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }
        return teacher;
    }
}
