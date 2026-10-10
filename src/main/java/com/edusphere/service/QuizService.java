package com.edusphere.service;

import com.edusphere.dao.QuizDAO;
import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.Teacher;

import java.util.List;

public class QuizService {

    private final QuizDAO quizDAO;
    private final CourseService courseService;
    private final TeacherService teacherService;

    public QuizService() {
        this.quizDAO = new QuizDAO();
        this.courseService = new CourseService();
        this.teacherService = new TeacherService();
    }


    /**
     * Create a quiz for a course.
     *
     * Only the teacher who owns the course
     * can create the quiz.
     */
    public Quiz createQuiz(
            Long teacherId,
            Long courseId,
            String title,
            String description,
            Integer maxAttempts)
            throws Exception {

        validateTeacherId(teacherId);
        validateCourseId(courseId);

        validateTitle(title);
        validateMaxAttempts(maxAttempts);

        /*
         * Verify teacher exists.
         */
        Teacher teacher =
                teacherService.findById(teacherId);

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Teacher not found."
            );
        }

        /*
         * Verify course exists.
         */
        Course course =
                courseService.findById(courseId);

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        /*
         * Ownership check.
         */
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not authorised to create a quiz for this course."
            );
        }

        if (description != null) {
            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }

            if (description.length() > 5000) {
                throw new IllegalArgumentException(
                        "Description cannot exceed 5000 characters."
                );
            }
        }

        Quiz quiz =
                new Quiz(
                        courseId,
                        title.trim(),
                        description,
                        maxAttempts
                );

        return quizDAO.createQuiz(quiz);
    }


    /**
     * Find quiz by ID.
     */
    public Quiz findById(Long quizId)
            throws Exception {

        if (quizId == null || quizId <= 0) {
            return null;
        }

        return quizDAO.findById(quizId);
    }


    /**
     * Find all quizzes belonging to a course.
     */
    public List<Quiz> findByCourse(Long courseId)
            throws Exception {

        validateCourseId(courseId);

        Course course =
                courseService.findById(courseId);

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        return quizDAO.findByCourse(courseId);
    }


    /**
     * Update a quiz.
     *
     * Only the teacher who owns the course
     * can update the quiz.
     */
    public Quiz updateQuiz(
            Long teacherId,
            Long quizId,
            String title,
            String description,
            Integer maxAttempts)
            throws Exception {

        validateTeacherId(teacherId);

        if (quizId == null || quizId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid quiz ID."
            );
        }

        validateTitle(title);
        validateMaxAttempts(maxAttempts);

        Quiz existing =
                quizDAO.findById(quizId);

        if (existing == null) {
            throw new IllegalArgumentException(
                    "Quiz not found."
            );
        }

        Course course =
                courseService.findById(
                        existing.getCourseId()
                );

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        /*
         * Ownership check.
         */
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not authorised to update this quiz."
            );
        }

        if (description != null) {
            description = description.trim();

            if (description.isBlank()) {
                description = null;
            }

            if (description != null &&
                    description.length() > 5000) {

                throw new IllegalArgumentException(
                        "Description cannot exceed 5000 characters."
                );
            }
        }

        existing.setTitle(
                title.trim()
        );

        existing.setDescription(
                description
        );

        existing.setMaxAttempts(
                maxAttempts
        );

        return quizDAO.updateQuiz(existing);
    }


    /**
     * Delete a quiz.
     *
     * Only the teacher who owns the course
     * can delete it.
     */
    public void deleteQuiz(
            Long teacherId,
            Long quizId)
            throws Exception {

        validateTeacherId(teacherId);

        if (quizId == null || quizId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid quiz ID."
            );
        }

        Quiz quiz =
                quizDAO.findById(quizId);

        if (quiz == null) {
            throw new IllegalArgumentException(
                    "Quiz not found."
            );
        }

        Course course =
                courseService.findById(
                        quiz.getCourseId()
                );

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        /*
         * Ownership check.
         */
        if (!course.getTeacherId().equals(teacherId)) {
            throw new IllegalArgumentException(
                    "You are not authorised to delete this quiz."
            );
        }

        boolean deleted =
                quizDAO.deleteQuiz(quizId);

        if (!deleted) {
            throw new IllegalArgumentException(
                    "Quiz could not be deleted."
            );
        }
    }


    /*
     * -------------------------
     * Validation helpers
     * -------------------------
     */

    private void validateTeacherId(Long teacherId) {

        if (teacherId == null || teacherId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid teacher ID."
            );
        }
    }


    private void validateCourseId(Long courseId) {

        if (courseId == null || courseId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid course ID."
            );
        }
    }


    private void validateTitle(String title) {

        if (title == null ||
                title.isBlank()) {

            throw new IllegalArgumentException(
                    "Quiz title is required."
            );
        }

        if (title.trim().length() > 200) {
            throw new IllegalArgumentException(
                    "Quiz title cannot exceed 200 characters."
            );
        }
    }


    private void validateMaxAttempts(
            Integer maxAttempts) {

        if (maxAttempts == null) {
            throw new IllegalArgumentException(
                    "Maximum attempts are required."
            );
        }

        if (maxAttempts <= 0) {
            throw new IllegalArgumentException(
                    "Maximum attempts must be greater than zero."
            );
        }

        if (maxAttempts > 100) {
            throw new IllegalArgumentException(
                    "Maximum attempts cannot exceed 100."
            );
        }
    }
}