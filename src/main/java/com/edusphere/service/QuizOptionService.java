package com.edusphere.service;

import com.edusphere.dao.QuizOptionDAO;
import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizOption;
import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Teacher;

import java.util.List;

public class QuizOptionService {

    private final QuizOptionDAO quizOptionDAO;
    private final QuizQuestionService quizQuestionService;
    private final QuizService quizService;
    private final CourseService courseService;
    private final TeacherService teacherService;

    public QuizOptionService() {
        this.quizOptionDAO = new QuizOptionDAO();
        this.quizQuestionService =
                new QuizQuestionService();
        this.quizService =
                new QuizService();
        this.courseService =
                new CourseService();
        this.teacherService =
                new TeacherService();
    }

    public QuizOption createOption(
            Long teacherId,
            Long questionId,
            String optionText,
            boolean correct) throws Exception {

        validateTeacher(teacherId);

        QuizQuestion question =
                getQuestion(questionId);

        verifyTeacherOwnsQuestion(
                teacherId,
                question
        );

        validateOptionText(optionText);

        /*
         * If this option is being created as the
         * correct answer, first clear any existing
         * correct answer.
         */
        if (correct) {

            quizOptionDAO.clearCorrectOptions(
                    questionId
            );
        }

        QuizOption option =
                new QuizOption(
                        questionId,
                        optionText.trim(),
                        correct
                );

        return quizOptionDAO.createOption(
                option
        );
    }

    public QuizOption findById(Long id)
            throws Exception {

        if (id == null) {
            throw new IllegalArgumentException(
                    "Option ID is required."
            );
        }

        return quizOptionDAO.findById(id);
    }

    public List<QuizOption> findByQuestion(
            Long questionId)
            throws Exception {

        QuizQuestion question =
                getQuestion(questionId);

        return quizOptionDAO.findByQuestion(
                question.getId()
        );
    }

    public QuizOption updateOption(
            Long teacherId,
            Long optionId,
            String optionText,
            boolean correct) throws Exception {

        validateTeacher(teacherId);

        if (optionId == null) {
            throw new IllegalArgumentException(
                    "Option ID is required."
            );
        }

        QuizOption existing =
                quizOptionDAO.findById(optionId);

        if (existing == null) {
            throw new IllegalArgumentException(
                    "Option not found."
            );
        }

        QuizQuestion question =
                getQuestion(
                        existing.getQuestionId()
                );

        verifyTeacherOwnsQuestion(
                teacherId,
                question
        );

        validateOptionText(optionText);

        /*
         * If this option becomes the correct answer,
         * remove the correct flag from all other
         * options belonging to this question.
         */
        if (correct) {

            quizOptionDAO.clearCorrectOptions(
                    existing.getQuestionId()
            );
        }

        existing.setOptionText(
                optionText.trim()
        );

        existing.setCorrect(correct);

        QuizOption updated =
                quizOptionDAO.updateOption(
                        existing
                );

        if (updated == null) {
            throw new IllegalArgumentException(
                    "Option could not be updated."
            );
        }

        return updated;
    }

    public void deleteOption(
            Long teacherId,
            Long optionId) throws Exception {

        validateTeacher(teacherId);

        if (optionId == null) {
            throw new IllegalArgumentException(
                    "Option ID is required."
            );
        }

        QuizOption option =
                quizOptionDAO.findById(optionId);

        if (option == null) {
            throw new IllegalArgumentException(
                    "Option not found."
            );
        }

        QuizQuestion question =
                getQuestion(
                        option.getQuestionId()
                );

        verifyTeacherOwnsQuestion(
                teacherId,
                question
        );

        boolean deleted =
                quizOptionDAO.deleteOption(
                        optionId
                );

        if (!deleted) {
            throw new IllegalArgumentException(
                    "Option could not be deleted."
            );
        }
    }

    /*
     * Explicitly mark an existing option as
     * the correct answer.
     */
    public QuizOption markCorrect(
            Long teacherId,
            Long optionId) throws Exception {

        validateTeacher(teacherId);

        if (optionId == null) {
            throw new IllegalArgumentException(
                    "Option ID is required."
            );
        }

        QuizOption option =
                quizOptionDAO.findById(optionId);

        if (option == null) {
            throw new IllegalArgumentException(
                    "Option not found."
            );
        }

        QuizQuestion question =
                getQuestion(
                        option.getQuestionId()
                );

        verifyTeacherOwnsQuestion(
                teacherId,
                question
        );

        quizOptionDAO.clearCorrectOptions(
                question.getId()
        );

        boolean marked =
                quizOptionDAO.markCorrect(
                        optionId
                );

        if (!marked) {
            throw new IllegalArgumentException(
                    "Option could not be marked as correct."
            );
        }

        return quizOptionDAO.findById(
                optionId
        );
    }

    private void validateTeacher(
            Long teacherId) throws Exception {

        if (teacherId == null) {
            throw new IllegalArgumentException(
                    "Teacher ID is required."
            );
        }

        Teacher teacher =
                teacherService.findById(
                        teacherId
                );

        if (teacher == null) {
            throw new IllegalArgumentException(
                    "Teacher not found."
            );
        }
    }

    private QuizQuestion getQuestion(
            Long questionId) throws Exception {

        if (questionId == null) {
            throw new IllegalArgumentException(
                    "Question ID is required."
            );
        }

        QuizQuestion question =
                quizQuestionService.findById(
                        questionId
                );

        if (question == null) {
            throw new IllegalArgumentException(
                    "Question not found."
            );
        }

        return question;
    }

    private void verifyTeacherOwnsQuestion(
            Long teacherId,
            QuizQuestion question)
            throws Exception {

        Quiz quiz =
                quizService.findById(
                        question.getQuizId()
                );

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

        if (!course.getTeacherId()
                .equals(teacherId)) {

            throw new SecurityException(
                    "You do not have permission to manage this option."
            );
        }
    }

    private void validateOptionText(
            String optionText) {

        if (optionText == null ||
                optionText.trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Option text is required."
            );
        }

        if (optionText.trim().length() > 1000) {

            throw new IllegalArgumentException(
                    "Option text cannot exceed 1000 characters."
            );
        }
    }
}