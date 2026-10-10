package com.edusphere.service;

import com.edusphere.dao.CategoryDAO;
import com.edusphere.dao.CourseDAO;
import com.edusphere.dao.TeacherDAO;
import com.edusphere.model.Course;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;

public class CourseService {

    private final CourseDAO courseDAO;
    private final TeacherDAO teacherDAO;
    private final CategoryDAO categoryDAO;

    public CourseService() {
        this.courseDAO = new CourseDAO();
        this.teacherDAO = new TeacherDAO();
        this.categoryDAO = new CategoryDAO();
    }

    public Course createCourse(
            Long teacherId,
            Long categoryId,
            String title,
            String description,
            BigDecimal price,
            String status) throws SQLException {

        // Validate teacher
        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException("Invalid teacher ID.");
        }

        teacherDAO.findById(teacherId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Teacher not found."));

        // Validate category
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("Invalid category ID.");
        }

        categoryDAO.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found."));

        // Validate title
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Course title is required.");
        }

        title = title.trim();

        if (title.length() > 200) {
            throw new IllegalArgumentException(
                    "Course title cannot exceed 200 characters.");
        }

        // Description
        if (description != null && description.isBlank()) {
            description = null;
        }

        // Price
        if (price == null) {
            price = BigDecimal.ZERO;
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Course price cannot be negative.");
        }

        // Status
        if (status == null || status.isBlank()) {
            status = "DRAFT";
        }

        status = status.trim().toUpperCase();

        if (!status.equals("DRAFT")
                && !status.equals("PUBLISHED")
                && !status.equals("ARCHIVED")) {

            throw new IllegalArgumentException(
                    "Invalid course status.");
        }

        Course course = new Course();

        course.setTeacherId(teacherId);
        course.setCategoryId(categoryId);
        course.setTitle(title);
        course.setDescription(description);
        course.setPrice(price);
        course.setStatus(status);

        return courseDAO.createCourse(course);
    }

    public Course findById(Long id) throws SQLException {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid course ID.");
        }

        return courseDAO.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Course not found."));
    }

    public List<Course> findCoursesByTeacher(Long teacherId) throws SQLException {

        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid teacher ID.");
        }

        return courseDAO.findByTeacherId(teacherId);
    }

    public List<Course> findPublishedCourses() throws SQLException {
        return courseDAO.findAllPublished();
    }

    public Course updateCourse(
            Long courseId,
            Long teacherId,
            Long categoryId,
            String title,
            String description,
            BigDecimal price,
            String status) throws SQLException {

        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid course ID."
            );
        }

        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid teacher ID."
            );
        }

        // Verify course exists
        Course existingCourse =
                courseDAO.findById(courseId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course not found."
                                ));

        // Ownership check
        if (!existingCourse.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not allowed to modify this course."
            );
        }

        // Validate category
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid category ID."
            );
        }

        categoryDAO.findById(categoryId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Category not found."
                        ));

        // Validate title
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Course title is required."
            );
        }

        title = title.trim();

        if (title.length() > 200) {
            throw new IllegalArgumentException(
                    "Course title cannot exceed 200 characters."
            );
        }

        // Description
        if (description != null &&
                description.isBlank()) {
            description = null;
        }

        // Price
        if (price == null) {
            price = BigDecimal.ZERO;
        }

        if (price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Course price cannot be negative."
            );
        }

        // Status
        if (status == null || status.isBlank()) {
            status = "DRAFT";
        }

        status = status.trim().toUpperCase();

        if (!status.equals("DRAFT")
                && !status.equals("PUBLISHED")
                && !status.equals("ARCHIVED")) {

            throw new IllegalArgumentException(
                    "Invalid course status."
            );
        }

        Course course = new Course();

        course.setId(courseId);
        course.setTeacherId(teacherId);
        course.setCategoryId(categoryId);
        course.setTitle(title);
        course.setDescription(description);
        course.setPrice(price);
        course.setStatus(status);

        return courseDAO.updateCourse(course);
    }

    public Course updateCourseStatus(
            Long courseId,
            Long teacherId,
            String status) throws SQLException {

        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid course ID."
            );
        }

        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid teacher ID."
            );
        }

        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException(
                    "Course status is required."
            );
        }

        status = status.trim().toUpperCase();

        if (!status.equals("DRAFT")
                && !status.equals("PUBLISHED")
                && !status.equals("ARCHIVED")) {

            throw new IllegalArgumentException(
                    "Invalid course status."
            );
        }

        Course course =
                courseDAO.findById(courseId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course not found."
                                ));

        /*
         * Security:
         * The logged-in teacher must own this course.
         */
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not allowed to modify this course."
            );
        }

        course.setStatus(status);

        return courseDAO.updateCourse(course);
    }
}