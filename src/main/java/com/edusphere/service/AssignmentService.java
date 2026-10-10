package com.edusphere.service;

import com.edusphere.dao.AssignmentDAO;
import com.edusphere.dao.CourseDAO;
import com.edusphere.dao.TeacherDAO;
import com.edusphere.model.Assignment;
import com.edusphere.model.Course;
import com.edusphere.model.Teacher;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AssignmentService {

    private final AssignmentDAO assignmentDAO;
    private final CourseDAO courseDAO;
    private final TeacherDAO teacherDAO;

    public AssignmentService() {
        this.assignmentDAO = new AssignmentDAO();
        this.courseDAO = new CourseDAO();
        this.teacherDAO = new TeacherDAO();
    }

    public Assignment createAssignment(
            Long teacherId,
            Long courseId,
            String title,
            String description,
            LocalDateTime deadline,
            BigDecimal maxMarks)
            throws Exception {

        validateTeacherId(teacherId);
        validateCourseId(courseId);
        validateTitle(title);
        validateMaxMarks(maxMarks);

        Optional<Teacher> optionalTeacher =
                teacherDAO.findById(teacherId);
        if(optionalTeacher.isEmpty()){
            throw new IllegalArgumentException("Teacher not found");
        }
        Teacher teacher=optionalTeacher.get();

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Teacher not found."
            );
        }

        Optional<Course> optionalCourse =
                courseDAO.findById(courseId);
        if(optionalCourse.isEmpty()){
            throw new IllegalArgumentException("Course not found");
        }
        Course course=optionalCourse.get();

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        // Teacher ownership check
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not allowed to create an assignment for this course."
            );
        }

        // Deadline must be in the future
        if (deadline != null &&
                deadline.isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Assignment deadline must be in the future."
            );
        }

        Assignment assignment =
                new Assignment(
                        courseId,
                        title.trim(),
                        normalizeDescription(description),
                        deadline,
                        maxMarks
                );

        return assignmentDAO.createAssignment(
                assignment
        );
    }

    public Assignment findById(Long id)
            throws Exception {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Assignment ID is required."
            );
        }

        return assignmentDAO.findById(id);
    }

    public List<Assignment> findByCourse(
            Long courseId)
            throws Exception {

        validateCourseId(courseId);

        Optional<Course> optionalCourse =
                courseDAO.findById(courseId);

        if(optionalCourse.isEmpty()){
            throw new IllegalArgumentException("Course not found");
        }
        Course course=optionalCourse.get();

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        return assignmentDAO.findByCourse(courseId);
    }

    public Assignment updateAssignment(
            Long teacherId,
            Long assignmentId,
            String title,
            String description,
            LocalDateTime deadline,
            BigDecimal maxMarks)
            throws Exception {

        validateTeacherId(teacherId);

        if (assignmentId == null) {
            throw new IllegalArgumentException(
                    "Assignment ID is required."
            );
        }

        validateTitle(title);
        validateMaxMarks(maxMarks);

        Optional<Teacher> optionalTeacher =
                teacherDAO.findById(teacherId);

        if(optionalTeacher.isEmpty()){
            throw new IllegalArgumentException("Teacher not found");
        }
        Teacher teacher=optionalTeacher.get();

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Teacher not found."
            );
        }

        Assignment existing =
                assignmentDAO.findById(
                        assignmentId
                );

        if (existing == null) {
            throw new IllegalArgumentException(
                    "Assignment not found."
            );
        }

        Optional<Course> optionalCourse =
                courseDAO.findById(
                        existing.getCourseId()
                );
        if(optionalCourse.isEmpty()){
            throw new IllegalArgumentException("Course not found");
        }
        Course course=optionalCourse.get();

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        // Ownership check
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not allowed to update this assignment."
            );
        }

        if (deadline != null &&
                deadline.isBefore(LocalDateTime.now())) {

            throw new IllegalArgumentException(
                    "Assignment deadline must be in the future."
            );
        }

        existing.setTitle(title.trim());

        existing.setDescription(
                normalizeDescription(description)
        );

        existing.setDeadline(deadline);

        existing.setMaxMarks(maxMarks);

        return assignmentDAO.updateAssignment(
                existing
        );
    }

    public boolean deleteAssignment(
            Long teacherId,
            Long assignmentId)
            throws Exception {

        validateTeacherId(teacherId);

        if (assignmentId == null) {
            throw new IllegalArgumentException(
                    "Assignment ID is required."
            );
        }

        Optional<Teacher> optionalTeacher =
                teacherDAO.findById(teacherId);
        if(optionalTeacher.isEmpty()){
            throw new IllegalArgumentException("Teacher not found");
        }
        Teacher teacher=optionalTeacher.get();

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Teacher not found."
            );
        }

        Assignment assignment =
                assignmentDAO.findById(
                        assignmentId
                );

        if (assignment == null) {
            throw new IllegalArgumentException(
                    "Assignment not found."
            );
        }

        Optional<Course>  optionalCourse =
                courseDAO.findById(
                        assignment.getCourseId()
                );
        if(optionalCourse.isEmpty()){
            throw new IllegalArgumentException("Course not found");
        }
        Course course=optionalCourse.get();

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        // Ownership check
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not allowed to delete this assignment."
            );
        }

        return assignmentDAO.deleteAssignment(
                assignmentId
        );
    }

    private void validateTeacherId(Long teacherId) {

        if (teacherId == null) {
            throw new IllegalArgumentException(
                    "Teacher ID is required."
            );
        }
    }

    private void validateCourseId(Long courseId) {

        if (courseId == null) {
            throw new IllegalArgumentException(
                    "Course ID is required."
            );
        }
    }

    private void validateTitle(String title) {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Assignment title is required."
            );
        }

        if (title.length() > 200) {
            throw new IllegalArgumentException(
                    "Assignment title must not exceed 200 characters."
            );
        }
    }

    private void validateMaxMarks(
            BigDecimal maxMarks) {

        if (maxMarks == null) {
            throw new IllegalArgumentException(
                    "Maximum marks are required."
            );
        }

        if (maxMarks.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Maximum marks must be greater than zero."
            );
        }
    }

    private String normalizeDescription(
            String description) {

        if (description == null ||
                description.isBlank()) {

            return null;
        }
        return description.trim();
    }
}