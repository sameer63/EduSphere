package com.edusphere.service;

import com.edusphere.dao.TeacherDAO;
import com.edusphere.dao.UserDAO;
import com.edusphere.model.Teacher;
import com.edusphere.model.User;

import java.sql.SQLException;

public class TeacherService {

    private final TeacherDAO teacherDAO;
    private final UserDAO userDAO;

    public TeacherService() {
        this.teacherDAO = new TeacherDAO();
        this.userDAO = new UserDAO();
    }

    public Teacher createTeacher(
            Long userId,
            String qualification,
            String bio) throws SQLException {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        User user = userDAO.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found."));

        if (!"TEACHER".equals(user.getRole())) {
            throw new IllegalArgumentException(
                    "User is not registered as a teacher."
            );
        }

        if (teacherDAO.findByUserId(userId).isPresent()) {
            throw new IllegalArgumentException(
                    "Teacher profile already exists."
            );
        }

        if (qualification != null && qualification.isBlank()) {
            qualification = null;
        }

        if (bio != null && bio.isBlank()) {
            bio = null;
        }

        Teacher teacher = new Teacher();

        teacher.setUserId(userId);
        teacher.setQualification(qualification);
        teacher.setBio(bio);

        return teacherDAO.createTeacher(teacher);
    }

    public Teacher findById(Long id) throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid teacher ID.");
        }

        return teacherDAO.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException("Teacher not found."));
    }

    public Teacher findByUserId(Long userId) throws SQLException {

        if (userId == null || userId <= 0) {
            throw new IllegalArgumentException("Invalid user ID.");
        }

        return teacherDAO.findByUserId(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Teacher profile not found."));
    }
}