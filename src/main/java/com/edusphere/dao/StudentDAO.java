package com.edusphere.dao;

import com.edusphere.dto.StudentProfile;
import com.edusphere.model.Student;
import com.edusphere.util.DBConnection;

import java.sql.*;
import java.util.Optional;

public class StudentDAO {

    private static final String INSERT_STUDENT= """
            INSERT INTO students (user_id,date_of_birth,phone)
            VALUES(?,?,?) RETURNING id,created_at
            """;

    private static final String FIND_BY_ID = """
            SELECT
                id,
                user_id,
                date_of_birth,
                phone,
                created_at
            FROM students
            WHERE id = ?
            """;

    private static final String FIND_BY_USER_ID = """
            SELECT
                id,
                user_id,
                date_of_birth,
                phone,
                created_at
            FROM students
            WHERE user_id = ?
            """;

    private static final String UPDATE_STUDENT = """
            UPDATE students
            SET
                date_of_birth = ?,
                phone = ?
            WHERE id = ?
            """;

    private static final String FIND_PROFILE_BY_USER_ID = """
        SELECT
            s.id AS student_id,
            u.id AS user_id,
            u.name,
            u.email,
            s.date_of_birth,
            s.phone
        FROM students s
        JOIN users u
            ON u.id = s.user_id
        WHERE s.user_id = ?
        """;
    public Student createStudent(
            Student student,
            Connection connection
    ) throws SQLException {

        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                INSERT_STUDENT
                        )
        ) {

            statement.setLong(
                    1,
                    student.getUserId()
            );

            if (student.getDateOfBirth() != null) {

                statement.setDate(
                        2,
                        Date.valueOf(
                                student.getDateOfBirth()
                        )
                );

            } else {

                statement.setNull(
                        2,
                        Types.DATE
                );
            }

            statement.setString(
                    3,
                    student.getPhone()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    student.setId(
                            resultSet.getLong("id")
                    );

                    Timestamp createdAt =
                            resultSet.getTimestamp(
                                    "created_at"
                            );

                    if (createdAt != null) {

                        student.setCreatedAt(
                                createdAt.toLocalDateTime()
                        );
                    }
                }
            }
        }

        return student;
    }

    public Student createStudent(Student student)throws SQLException{
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement statement = conn.prepareStatement(INSERT_STUDENT)) {

            statement.setLong(1, student.getUserId());
            if (student.getDateOfBirth() != null) {
                statement.setDate(
                        2,
                        Date.valueOf(
                                student.getDateOfBirth()
                        )
                );
            } else {
                statement.setNull(
                        2,
                        Types.DATE
                );
            }
            statement.setString(3, student.getPhone());
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    student.setId(rs.getLong("id"));
                    Timestamp createdAt =
                            rs.getTimestamp(
                                    "created_at"
                            );

                    if (createdAt != null) {
                        student.setCreatedAt(
                                createdAt.toLocalDateTime()
                        );
                    }

                }

            }
        }
        return student;
    }
    public Optional<Student> findById(Long id)throws SQLException{
        try (Connection conn=DBConnection.getConnection();
            PreparedStatement statement=conn.prepareStatement(FIND_BY_ID)){
            statement.setLong(1,id);
            try (ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return Optional.of(mapToStudent(resultSet));
                }
            }

        }
        return Optional.empty();
    }

    public Optional<Student> findByUserId(Long userId)throws SQLException{
        try (Connection conn=DBConnection.getConnection();
           PreparedStatement statement=conn.prepareStatement(FIND_BY_USER_ID)){
            statement.setLong(1,userId);

            try (ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return Optional.of(mapToStudent(resultSet));
                }

            }

        }
        return Optional.empty();

    }

    public boolean updateStudent(Student student)throws SQLException{
        try (Connection conn=DBConnection.getConnection();
        PreparedStatement statement=conn.prepareStatement(UPDATE_STUDENT)){
            if (student.getDateOfBirth() != null) {
                statement.setDate(
                        1,
                        Date.valueOf(
                                student.getDateOfBirth()
                        )
                );
            } else {
                statement.setNull(
                        1,
                        Types.DATE
                );
            }
            statement.setString(2,student.getPhone());
            statement.setLong(3,student.getId());

            return statement.executeUpdate()==1;

        }

    }

    private Student mapToStudent(
            ResultSet resultSet
    ) throws SQLException {

        Student student = new Student();

        student.setId(
                resultSet.getLong("id")
        );

        student.setUserId(
                resultSet.getLong("user_id")
        );

        Date dateOfBirth =
                resultSet.getDate("date_of_birth");

        if (dateOfBirth != null) {

            student.setDateOfBirth(
                    dateOfBirth.toLocalDate()
            );
        }

        student.setPhone(
                resultSet.getString("phone")
        );

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {

            student.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }
        return student;
    }

    public Optional<StudentProfile> findProfileByUserId(Long userId)throws SQLException {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_PROFILE_BY_USER_ID)) {
            statement.setLong(1, userId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    StudentProfile profile = new StudentProfile();
                    profile.setStudentId(resultSet.getLong("student_id"));
                    profile.setUserId(resultSet.getLong("user_id"));
                    profile.setName(resultSet.getString("name"));
                    profile.setEmail(resultSet.getString("email"));
                    Date dateOfBirth =
                            resultSet.getDate(
                                    "date_of_birth"
                            );

                    if (dateOfBirth != null) {
                        profile.setDateOfBirth(
                                dateOfBirth.toLocalDate()
                        );
                    }
                    profile.setPhone(
                            resultSet.getString("phone")
                    );
                    return Optional.of(profile);
                }
            }

        }
        return Optional.empty();
    }
}
