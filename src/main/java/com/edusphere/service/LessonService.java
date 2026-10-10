package com.edusphere.service;

import com.edusphere.dao.CourseDAO;
import com.edusphere.dao.LessonDAO;
import com.edusphere.dao.TeacherDAO;
import com.edusphere.model.Course;
import com.edusphere.model.Lesson;
import com.edusphere.model.Teacher;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class LessonService {

    private final LessonDAO lessonDAO;
    private final CourseDAO courseDAO;
    private final TeacherDAO teacherDAO;

    public LessonService() {
        this.lessonDAO = new LessonDAO();
        this.courseDAO = new CourseDAO();
        this.teacherDAO = new TeacherDAO();
    }

    /**
     * Create a lesson for a teacher's course.
     */
    public Lesson createLesson(
            Long teacherId,
            Long courseId,
            String title,
            String content,
            Integer position) throws SQLException {

        // Validate teacher ID
        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid teacher ID."
            );
        }

        // Validate course ID
        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid course ID."
            );
        }

        // Validate title
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Lesson title is required."
            );
        }

        title = title.trim();

        if (title.length() > 200) {
            throw new IllegalArgumentException(
                    "Lesson title cannot exceed 200 characters."
            );
        }

        // Validate content
        if (content != null && content.isBlank()) {
            content = null;
        }

        // Validate position
        if (position == null || position <= 0) {
            throw new IllegalArgumentException(
                    "Lesson position must be greater than zero."
            );
        }

        // Verify teacher exists
        teacherDAO.findById(teacherId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Teacher not found."
                        ));

        // Verify course exists
        Course course =
                courseDAO.findById(courseId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Course not found."
                                ));

        /*
         * IMPORTANT:
         * Verify that this course actually belongs
         * to the logged-in teacher.
         */
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not allowed to add lessons to this course."
            );
        }

        Lesson lesson = new Lesson();

        lesson.setCourseId(courseId);
        lesson.setTitle(title);
        lesson.setContent(content);
        lesson.setPosition(position);

        return lessonDAO.createLesson(lesson);
    }

    /**
     * Find lesson by ID.
     */
    public Lesson findById(Long id) {

        if (id == null || id <= 0) {
            throw new IllegalArgumentException(
                    "Invalid lesson ID."
            );
        }

        return lessonDAO.findById(id)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Lesson not found."
                        ));
    }

    /**
     * Get all lessons belonging to a course.
     */
    public List<Lesson> findByCourse(Long courseId) throws SQLException {

        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid course ID."
            );
        }

        // Verify course exists first
        courseDAO.findById(courseId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Course not found."
                        ));

        return lessonDAO.findByCourse(courseId);
    }

    /**
     * Verify that a teacher owns the course
     * associated with a lesson.
     */
    public boolean teacherOwnsLesson(
            Long teacherId,
            Long lessonId) throws SQLException {

        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid teacher ID."
            );
        }

        Lesson lesson = findById(lessonId);

        Course course =
                courseDAO.findById(
                        lesson.getCourseId()
                ).orElseThrow(() ->
                        new IllegalArgumentException(
                                "Course not found."
                        ));

        return course.getTeacherId()
                .equals(teacherId);
    }

    public boolean deleteLesson(Long teacherId,
                                Long lessonId)
            throws Exception {

        if (teacherId == null) {
            throw new IllegalArgumentException(
                    "Teacher ID is required."
            );
        }

        if (lessonId == null) {
            throw new IllegalArgumentException(
                    "Lesson ID is required."
            );
        }

        // Verify teacher exists
        Optional<Teacher> optionalTeacher = teacherDAO.findById(teacherId);
        Teacher teacher=optionalTeacher.get();

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Teacher not found."
            );
        }

        // Find lesson
        Optional<Lesson> optionalLesson = lessonDAO.findById(lessonId);
        Lesson lesson=optionalLesson.get();

        if (lesson == null) {
            throw new IllegalArgumentException(
                    "Lesson not found."
            );
        }

        // Find course
        Optional<Course> optionalCourse = courseDAO.findById(
                lesson.getCourseId()
        );

        Course course=optionalCourse.get();

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        // Ownership check
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not allowed to delete this lesson."
            );
        }

        return lessonDAO.deleteLesson(lessonId);
    }

    public Lesson updateLesson(Long teacherId,
                               Long lessonId,
                               String title,
                               String content,
                               Integer position)
            throws Exception {

        if (teacherId == null) {
            throw new IllegalArgumentException(
                    "Teacher ID is required."
            );
        }

        if (lessonId == null) {
            throw new IllegalArgumentException(
                    "Lesson ID is required."
            );
        }

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException(
                    "Lesson title is required."
            );
        }

        if (title.length() > 200) {
            throw new IllegalArgumentException(
                    "Lesson title must not exceed 200 characters."
            );
        }

        if (position == null || position <= 0) {
            throw new IllegalArgumentException(
                    "Lesson position must be greater than 0."
            );
        }

        // Verify teacher exists
        Optional<Teacher> optionalTeacher = teacherDAO.findById(teacherId);
        Teacher teacher=optionalTeacher.get();

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Teacher not found."
            );
        }
        // Find lesson
        Optional<Lesson> existingOptionalLesson = lessonDAO.findById(lessonId);
        Lesson existingLesson=existingOptionalLesson.get();

        if (existingLesson == null) {
            throw new IllegalArgumentException(
                    "Lesson not found."
            );
        }

        // Find course
        Optional<Course> optionalCourse = courseDAO.findById(
                existingLesson.getCourseId()
        );

        Course course=optionalCourse.get();

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        // Ownership check
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not allowed to update this lesson."
            );
        }

        existingLesson.setTitle(title.trim());
        existingLesson.setContent(
                content == null || content.isBlank()
                        ? null
                        : content.trim()
        );
        existingLesson.setPosition(position);

        return lessonDAO.updateLesson(existingLesson);
    }
}