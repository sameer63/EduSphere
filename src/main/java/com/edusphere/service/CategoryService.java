package com.edusphere.service;

import com.edusphere.dao.CategoryDAO;
import com.edusphere.model.Category;

import java.sql.SQLException;
import java.util.List;

public class CategoryService {

    private final CategoryDAO categoryDAO;

    public CategoryService() {
        this.categoryDAO = new CategoryDAO();
    }

    public Category createCategory(String name, String description) throws SQLException {

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Category name is required.");
        }

        name = name.trim();

        if (name.length() > 100) {
            throw new IllegalArgumentException(
                    "Category name cannot exceed 100 characters."
            );
        }

        if (description != null && description.isBlank()) {
            description = null;
        }

        Category category = new Category();
        category.setName(name);
        category.setDescription(description);

        return categoryDAO.createCategory(category);
    }

    public Category findById(Long id) throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }

        return categoryDAO.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Category not found."));
    }

    public List<Category> findAll() throws SQLException {
        return categoryDAO.findAll();
    }
}