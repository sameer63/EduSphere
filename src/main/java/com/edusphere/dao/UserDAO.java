package com.edusphere.dao;

import com.edusphere.model.User;
import com.edusphere.util.DBConnection;

import java.sql.*;
import java.util.Map;
import java.util.Optional;

public class UserDAO {
    private static final String INSERT_USER= """
            INSERT INTO users
                (name, email, password_hash, role)
            VALUES
                (?, ?, ?, ?)
            RETURNING id, created_at, updated_at
            """;

    private static final String FIND_BY_EMAIL= """
            SELECT 
                id,
                name,
                email,
                password_hash,
                role,
                is_active,
                created_at,
                updated_at
            FROM users WHERE email=?
            """;

    private static final String FIND_BY_ID = """
            SELECT
                id,
                name,
                email,
                password_hash,
                role,
                is_active,
                created_at,
                updated_at
            FROM users
            WHERE id = ?
            """;

    public User createUser(
            User user,
            Connection connection
    ) throws SQLException {

        try (
                PreparedStatement statement =
                        connection.prepareStatement(
                                INSERT_USER
                        )
        ) {

            statement.setString(
                    1,
                    user.getName()
            );

            statement.setString(
                    2,
                    user.getEmail()
            );

            statement.setString(
                    3,
                    user.getPasswordHash()
            );

            statement.setString(
                    4,
                    user.getRole()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {

                    user.setId(
                            resultSet.getLong("id")
                    );

                    Timestamp createdAt =
                            resultSet.getTimestamp(
                                    "created_at"
                            );

                    if (createdAt != null) {

                        user.setCreatedAt(
                                createdAt.toLocalDateTime()
                        );
                    }

                    Timestamp updatedAt =
                            resultSet.getTimestamp(
                                    "updated_at"
                            );

                    if (updatedAt != null) {

                        user.setUpdatedAt(
                                updatedAt.toLocalDateTime()
                        );
                    }
                }
            }
        }

        return user;
    }

    public User createUser(User user)throws SQLException {
        try (Connection connection= DBConnection.getConnection();
             PreparedStatement statement=connection.prepareStatement(INSERT_USER)
        ){
            statement.setString(1,user.getName());
            statement.setString(2,user.getEmail());
            statement.setString(3,user.getPasswordHash());
            statement.setString(4,user.getRole());

            try(ResultSet resultSet=statement.executeQuery()) {
                if(resultSet.next()){
                    user.setId(resultSet.getLong("id"));
                    user.setCreatedAt(resultSet.getTimestamp("created_at").toLocalDateTime());
                    user.setUpdatedAt(resultSet.getTimestamp("updated_at").toLocalDateTime());
                }
            }

        }
       return user;
    }



    public Optional<User> findByEmail(String email)throws SQLException{
        try (Connection connection=DBConnection.getConnection();
             PreparedStatement statement=connection.prepareStatement(FIND_BY_EMAIL)){
             statement.setString(1,email);
            try (ResultSet resultSet= statement.executeQuery()){
                if (resultSet.next()){
                    return Optional.of(mapUser(resultSet));
                }

            }
        }
        return Optional.empty();
    }

    public Optional<User> findById(Long id)
            throws SQLException {

        try (
                Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(FIND_BY_ID)
        ) {

            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return Optional.of(mapUser(resultSet));
                }
            }
        }

        return Optional.empty();
    }

    private User mapUser(ResultSet resultSet)throws SQLException{
        User user=new User();

        user.setId(resultSet.getLong("id"));
        user.setName(resultSet.getString("name"));
        user.setEmail(resultSet.getString("email"));
        user.setPasswordHash(resultSet.getString("password_hash"));
        user.setRole(resultSet.getString("role"));
        user.setActive(resultSet.getBoolean("is_active"));

        Timestamp createdAt =
                resultSet.getTimestamp("created_at");
        if (createdAt != null) {
            user.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }

        Timestamp updatedAt =
                resultSet.getTimestamp("updated_at");

        if (updatedAt != null) {
            user.setUpdatedAt(
                    updatedAt.toLocalDateTime()
            );
        }
        return user;
    }
}
