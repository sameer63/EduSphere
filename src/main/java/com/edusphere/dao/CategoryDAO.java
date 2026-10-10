package com.edusphere.dao;

import com.edusphere.model.Category;
import com.edusphere.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CategoryDAO {

    private static final String INSERT_CATEGORY= """
            INSERT INTO categories (name,description)
            VALUES(?,?)
            RETURNING id,created_at
            """;
    private static final String FIND_BY_ID = """
            SELECT
                id,
                name,
                description,
                created_at
            FROM categories
            WHERE id = ?
            """;
    private static final String FIND_ALL = """
            SELECT
                id,
                name,
                description,
                created_at
            FROM categories
            ORDER BY name
            """;

    public Category createCategory(Category category)throws SQLException {
        try (Connection connection= DBConnection.getConnection();
             PreparedStatement statement=connection.prepareStatement(INSERT_CATEGORY)){
            statement.setString(1,category.getName());
            statement.setString(2, category.getDescription());

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    category.setId(resultSet.getLong("id"));
                    Timestamp createdAt =
                            resultSet.getTimestamp(
                                    "created_at"
                            );

                    if (createdAt != null) {

                        category.setCreatedAt(
                                createdAt.toLocalDateTime()
                        );
                    }
                }

            }

        }
        return category;
    }

    public Optional<Category> findById(Long id)throws SQLException{
        try(Connection connection=DBConnection.getConnection();
        PreparedStatement statement=connection.prepareStatement(FIND_BY_ID)){
            statement.setLong(1,id);

            try(ResultSet resultSet=statement.executeQuery()){
                if(resultSet.next()){
                    return Optional.of(mapCategory(resultSet));

                }

            }

        }
        return Optional.empty();
    }

    public List<Category> findAll()throws SQLException{
        List<Category> categories=new ArrayList<>();

        try(Connection connection=DBConnection.getConnection();
        PreparedStatement statement=connection.prepareStatement(FIND_ALL);
            ResultSet resultSet=statement.executeQuery()){

            while (resultSet.next()){
                categories.add(mapCategory(resultSet));
            }
        }
        return categories;
    }
    private Category mapCategory(
            ResultSet resultSet
    ) throws SQLException {
        Category category =
                new Category();

        category.setId(
                resultSet.getLong("id")
        );
        category.setName(
                resultSet.getString("name")
        );
        category.setDescription(
                resultSet.getString("description")
        );
        Timestamp createdAt =
                resultSet.getTimestamp("created_at");

        if (createdAt != null) {
            category.setCreatedAt(
                    createdAt.toLocalDateTime()
            );
        }
        return category;
    }
}
