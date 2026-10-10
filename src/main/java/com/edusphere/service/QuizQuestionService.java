package com.edusphere.service;

import com.edusphere.dao.QuizQuestionDAO;
import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Teacher;

import java.util.List;

public class QuizQuestionService {

    private final QuizQuestionDAO quizQuestionDAO;
    private final QuizService quizService;
    private final CourseService courseService;
    private final TeacherService teacherService;

    public QuizQuestionService() {
        this.quizQuestionDAO = new QuizQuestionDAO();
        this.quizService = new QuizService();
        this.courseService = new CourseService();
        this.teacherService = new TeacherService();
    }

    public QuizQuestion createQuestion(
            Long teacherId,
            Long quizId,
            String questionText,
            Integer marks,
            Integer position) throws Exception {

        validateTeacher(teacherId);

        Quiz quiz = getQuiz(quizId);

        verifyTeacherOwnsQuiz(teacherId, quiz);

        validateQuestion(
                questionText,
                marks,
                position
        );

        QuizQuestion question =
                new QuizQuestion(
                        quizId,
                        questionText.trim(),
                        marks,
                        position
                );

        return quizQuestionDAO.createQuestion(question);
    }

    public QuizQuestion findById(Long id)
            throws Exception {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Question ID is required."
            );
        }

        return quizQuestionDAO.findById(id);
    }

    public List<QuizQuestion> findByQuiz(Long quizId)
            throws Exception {

        if (quizId == null) {
            throw new IllegalArgumentException(
                    "Quiz ID is required."
            );
        }

        Quiz quiz = quizService.findById(quizId);

        if (quiz == null) {
            throw new IllegalArgumentException(
                    "Quiz not found."
            );
        }

        return quizQuestionDAO.findByQuiz(quizId);
    }

    public QuizQuestion updateQuestion(
            Long teacherId,
            Long questionId,
            String questionText,
            Integer marks,
            Integer position) throws Exception {

        validateTeacher(teacherId);

        if (questionId == null) {
            throw new IllegalArgumentException(
                    "Question ID is required."
            );
        }

        QuizQuestion existing =
                quizQuestionDAO.findById(questionId);

        if (existing == null) {
            throw new IllegalArgumentException(
                    "Question not found."
            );
        }

        Quiz quiz =
                getQuiz(existing.getQuizId());

        verifyTeacherOwnsQuiz(
                teacherId,
                quiz
        );

        validateQuestion(
                questionText,
                marks,
                position
        );

        existing.setQuestionText(
                questionText.trim()
        );

        existing.setMarks(marks);
        existing.setPosition(position);

        QuizQuestion updated =
                quizQuestionDAO.updateQuestion(existing);

        if (updated == null) {
            throw new IllegalArgumentException(
                    "Question could not be updated."
            );
        }

        return updated;
    }

    public void deleteQuestion(
            Long teacherId,
            Long questionId) throws Exception {

        validateTeacher(teacherId);

        if (questionId == null) {
            throw new IllegalArgumentException(
                    "Question ID is required."
            );
        }

        QuizQuestion question =
                quizQuestionDAO.findById(questionId);

        if (question == null) {
            throw new IllegalArgumentException(
                    "Question not found."
            );
        }

        Quiz quiz =
                getQuiz(question.getQuizId());

        verifyTeacherOwnsQuiz(
                teacherId,
                quiz
        );

        boolean deleted =
                quizQuestionDAO.deleteQuestion(
                        questionId
                );

        if (!deleted) {
            throw new IllegalArgumentException(
                    "Question could not be deleted."
            );
        }
    }

    private void validateTeacher(Long teacherId)
            throws Exception {

        if (teacherId == null) {
            throw new IllegalArgumentException(
                    "Teacher ID is required."
            );
        }

        Teacher teacher =
                teacherService.findById(teacherId);

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Teacher not found."
            );
        }
    }

    private Quiz getQuiz(Long quizId)
            throws Exception {

        if (quizId == null) {
            throw new IllegalArgumentException(
                    "Quiz ID is required."
            );
        }

        Quiz quiz =
                quizService.findById(quizId);

        if (quiz == null) {
            throw new IllegalArgumentException(
                    "Quiz not found."
            );
        }

        return quiz;
    }

    private void verifyTeacherOwnsQuiz(
            Long teacherId,
            Quiz quiz) throws Exception {

        Course course =
                courseService.findById(
                        quiz.getCourseId()
                );

        if (course == null) {
            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        if (!course.getTeacherId().equals(teacherId)) {
            throw new SecurityException(
                    "You do not have permission to manage this quiz."
            );
        }
    }

    private void validateQuestion(
            String questionText,
            Integer marks,
            Integer position) {

        if (questionText == null ||
                questionText.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Question text is required."
            );
        }

        if (questionText.trim().length() > 5000) {
            throw new IllegalArgumentException(
                    "Question text cannot exceed 5000 characters."
            );
        }

        if (marks == null || marks <= 0) {
            throw new IllegalArgumentException(
                    "Marks must be greater than 0."
            );
        }

        if (position == null || position <= 0) {
            throw new IllegalArgumentException(
                    "Position must be greater than 0."
            );
        }
    }
}