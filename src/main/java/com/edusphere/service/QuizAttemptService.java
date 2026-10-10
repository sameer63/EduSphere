package com.edusphere.service;

import com.edusphere.dao.QuizAttemptAnswerDAO;
import com.edusphere.dao.QuizAttemptDAO;
import com.edusphere.dto.QuizAnalyticsDTO;
import com.edusphere.dto.QuizAttemptSummaryDTO;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizAttempt;
import com.edusphere.model.QuizAttemptAnswer;
import com.edusphere.model.QuizOption;
import com.edusphere.model.QuizQuestion;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class QuizAttemptService {

    private final QuizAttemptDAO quizAttemptDAO;
    private final QuizAttemptAnswerDAO quizAttemptAnswerDAO;
    private final QuizService quizService;
    private final QuizQuestionService quizQuestionService;
    private final QuizOptionService quizOptionService;
    private final StudentService studentService;
    private final EnrolmentService enrolmentService;

    public QuizAttemptService() {
        this.quizAttemptDAO = new QuizAttemptDAO();
        this.quizAttemptAnswerDAO = new QuizAttemptAnswerDAO();
        this.quizService = new QuizService();
        this.quizQuestionService = new QuizQuestionService();
        this.quizOptionService = new QuizOptionService();
        this.studentService = new StudentService();
        this.enrolmentService = new EnrolmentService();
    }

    /*
     * ============================================================
     * START / RESUME ATTEMPT
     * ============================================================
     */

    public QuizAttempt startAttempt(
            Long studentId,
            Long quizId) throws Exception {

        validateIds(studentId, quizId);

        validateStudent(studentId);

        Quiz quiz = getQuiz(quizId);

        validateStudentEnrolment(studentId, quiz.getCourseId());

        /*
         * If the student already has an unfinished attempt,
         * return that attempt instead of creating another one.
         */
        Optional<QuizAttempt> activeAttempt =
                quizAttemptDAO.findActiveAttempt(
                        quizId,
                        studentId
                );

        if (activeAttempt.isPresent()) {
            return activeAttempt.get();
        }

        int attemptCount =
                quizAttemptDAO.getAttemptCount(
                        quizId,
                        studentId
                );

        if (attemptCount >= quiz.getMaxAttempts()) {
            throw new IllegalStateException(
                    "Maximum number of quiz attempts reached."
            );
        }

        int nextAttemptNumber =
                quizAttemptDAO.getNextAttemptNumber(
                        quizId,
                        studentId
                );

        QuizAttempt attempt =
                new QuizAttempt(
                        quizId,
                        studentId,
                        nextAttemptNumber
                );

        return quizAttemptDAO.createAttempt(attempt);
    }

    /*
     * ============================================================
     * FIND ATTEMPTS
     * ============================================================
     */

    public Optional<QuizAttempt> findById(Long attemptId)
            throws Exception {

        if (attemptId == null) {
            return Optional.empty();
        }

        return quizAttemptDAO.findById(attemptId);
    }

    public List<QuizAttempt> findByQuizAndStudent(
            Long quizId,
            Long studentId) throws Exception {

        validateIds(studentId, quizId);

        validateStudent(studentId);
        getQuiz(quizId);

        return quizAttemptDAO.findByQuizAndStudent(
                quizId,
                studentId
        );
    }

    public Optional<QuizAttempt> findActiveAttempt(
            Long quizId,
            Long studentId) throws Exception {

        validateIds(studentId, quizId);

        validateStudent(studentId);
        getQuiz(quizId);

        return quizAttemptDAO.findActiveAttempt(
                quizId,
                studentId
        );
    }

    /*
     * ============================================================
     * ANSWER QUESTION
     * ============================================================
     */

    public QuizAttemptAnswer answerQuestion(
            Long studentId,
            Long attemptId,
            Long questionId,
            Long optionId) throws Exception {

        if (studentId == null ||
                attemptId == null ||
                questionId == null ||
                optionId == null) {

            throw new IllegalArgumentException(
                    "Student, attempt, question and option are required."
            );
        }

        validateStudent(studentId);

        QuizAttempt attempt =
                getAttempt(attemptId);

        /*
         * The attempt must belong to the logged-in student.
         */
        if (!attempt.getStudentId().equals(studentId)) {
            throw new SecurityException(
                    "You are not allowed to modify this attempt."
            );
        }

        /*
         * Submitted attempts are read-only.
         */
        if (attempt.getSubmittedAt() != null) {
            throw new IllegalStateException(
                    "This quiz attempt has already been submitted."
            );
        }

        Quiz quiz =
                getQuiz(attempt.getQuizId());

        /*
         * Student must still be enrolled in the course.
         */
        validateStudentEnrolment(
                studentId,
                quiz.getCourseId()
        );

        /*
         * Verify that the question actually belongs
         * to this quiz.
         */
        QuizQuestion question =
                quizQuestionService.findById(questionId);
        if(question==null){
            throw new IllegalArgumentException("Question not found");
        }

        if (!question.getQuizId()
                .equals(attempt.getQuizId())) {

            throw new IllegalArgumentException(
                    "Question does not belong to this quiz."
            );
        }

        /*
         * Verify that the selected option exists.
         */
        QuizOption option =
                quizOptionService.findById(optionId);
        if(option==null){
            throw new IllegalArgumentException("Option not found");
        }

        /*
         * Most important validation:
         * option must belong to the selected question.
         */
        if (!option.getQuestionId()
                .equals(questionId)) {

            throw new IllegalArgumentException(
                    "Selected option does not belong to this question."
            );
        }

        /*
         * If the student already answered this question,
         * update the answer instead of creating a duplicate.
         */
        Optional<QuizAttemptAnswer> existing =
                quizAttemptAnswerDAO
                        .findByAttemptAndQuestion(
                                attemptId,
                                questionId
                        );

        if (existing.isPresent()) {

            quizAttemptAnswerDAO.updateAnswer(
                    attemptId,
                    questionId,
                    optionId
            );

            return quizAttemptAnswerDAO
                    .findByAttemptAndQuestion(
                            attemptId,
                            questionId
                    )
                    .orElseThrow(() ->
                            new IllegalStateException(
                                    "Updated answer could not be found."
                            )
                    );
        }

        QuizAttemptAnswer answer =
                new QuizAttemptAnswer(
                        attemptId,
                        questionId,
                        optionId
                );

        return quizAttemptAnswerDAO.createAnswer(answer);
    }

    /*
     * ============================================================
     * GET ANSWERS
     * ============================================================
     */

    public List<QuizAttemptAnswer> findAnswers(
            Long studentId,
            Long attemptId) throws Exception {

        if (studentId == null || attemptId == null) {
            throw new IllegalArgumentException(
                    "Student and attempt are required."
            );
        }

        validateStudent(studentId);

        QuizAttempt attempt =
                getAttempt(attemptId);

        if (!attempt.getStudentId().equals(studentId)) {
            throw new SecurityException(
                    "You are not allowed to view this attempt."
            );
        }

        return quizAttemptAnswerDAO.findByAttempt(
                attemptId
        );
    }

    /*
     * ============================================================
     * SUBMIT ATTEMPT
     * ============================================================
     */

    public QuizAttempt submitAttempt(
            Long studentId,
            Long attemptId) throws Exception {

        if (studentId == null || attemptId == null) {
            throw new IllegalArgumentException(
                    "Student and attempt are required."
            );
        }

        validateStudent(studentId);

        QuizAttempt attempt =
                getAttempt(attemptId);

        if (!attempt.getStudentId().equals(studentId)) {
            throw new SecurityException(
                    "You are not allowed to submit this attempt."
            );
        }

        if (attempt.getSubmittedAt() != null) {
            throw new IllegalStateException(
                    "This quiz attempt has already been submitted."
            );
        }

        Quiz quiz =
                getQuiz(attempt.getQuizId());

        validateStudentEnrolment(
                studentId,
                quiz.getCourseId()
        );

        /*
         * Retrieve every answer submitted for this attempt.
         */
        List<QuizAttemptAnswer> answers =
                quizAttemptAnswerDAO.findByAttempt(
                        attemptId
                );

        BigDecimal score =
                calculateScore(
                        quiz.getId(),
                        answers
                );

        boolean submitted =
                quizAttemptDAO.submitAttempt(
                        attemptId,
                        score
                );

        if (!submitted) {
            throw new IllegalStateException(
                    "Quiz attempt could not be submitted."
            );
        }

        return quizAttemptDAO.findById(attemptId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Submitted attempt could not be found."
                        )
                );
    }

    public List<QuizAttempt> findByQuiz(Long quizId)
            throws Exception {

        if (quizId == null) {
            throw new IllegalArgumentException(
                    "Quiz ID is required."
            );
        }

        getQuiz(quizId);

        return quizAttemptDAO.findByQuiz(quizId);
    }


    public QuizAnalyticsDTO getQuizAnalytics(
            Long quizId,
            BigDecimal totalMarks
    ) throws Exception {

        if (quizId == null) {
            throw new IllegalArgumentException(
                    "Quiz ID is required."
            );
        }

        if (totalMarks == null ||
                totalMarks.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "Total marks must be greater than zero."
            );
        }

        getQuiz(quizId);

        return quizAttemptDAO.getQuizAnalytics(
                quizId,
                totalMarks
        );
    }

    public List<QuizAttemptSummaryDTO> findByQuizWithStudentDetails(
            Long quizId
    ) throws Exception {

        if (quizId == null) {
            throw new IllegalArgumentException(
                    "Quiz ID is required."
            );
        }

        getQuiz(quizId);

        return quizAttemptDAO.findByQuizWithStudentDetails(quizId);
    }

    /*
     * ============================================================
     * SCORE CALCULATION
     * ============================================================
     */

    private BigDecimal calculateScore(
            Long quizId,
            List<QuizAttemptAnswer> answers)
            throws Exception {

        BigDecimal score =
                BigDecimal.ZERO;

        for (QuizAttemptAnswer answer : answers) {

            QuizQuestion question =
                    quizQuestionService.findById(
                                    answer.getQuestionId());
            if(question==null){
                throw new IllegalArgumentException("Question not found while calculating score");
            }

            /*
             * Extra protection:
             * the question must belong to this quiz.
             */
            if (!question.getQuizId().equals(quizId)) {
                throw new IllegalStateException(
                        "Answer contains a question from another quiz."
                );
            }

            QuizOption option =
                    quizOptionService.findById(
                                    answer.getOptionId());
            if(option==null){
                throw new IllegalArgumentException("Option not found while calculating score");
            }


            /*
             * Extra protection:
             * option must belong to the question.
             */
            if (!option.getQuestionId()
                    .equals(question.getId())) {

                throw new IllegalStateException(
                        "Answer contains an invalid option."
                );
            }

            /*
             * Single-correct-answer MCQ:
             * if the selected option is correct,
             * add the question's marks.
             */
            if (option.isCorrect()) {

                score = score.add(
                        BigDecimal.valueOf(
                                question.getMarks()
                        )
                );
            }
        }

        return score;
    }

    /*
     * ============================================================
     * VALIDATION HELPERS
     * ============================================================
     */

    private void validateStudent(Long studentId)
            throws Exception {

        studentService.findById(studentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Student not found."
                        )
                );
    }

    private Quiz getQuiz(Long quizId)
            throws Exception {

        Quiz quiz=quizService.findById(quizId);
        if(quiz==null){
            throw new IllegalArgumentException("Quiz not found");
        }
        return quiz;
    }

    private QuizAttempt getAttempt(Long attemptId)
            throws Exception {

        return quizAttemptDAO.findById(attemptId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Quiz attempt not found."
                        )
                );
    }

    private void validateStudentEnrolment(
            Long studentId,
            Long courseId) throws Exception {

        boolean enrolled =
                enrolmentService.isStudentEnrolled(
                        studentId,
                        courseId
                );

        if (!enrolled) {
            throw new SecurityException(
                    "Student is not enrolled in this course."
            );
        }
    }

    private void validateIds(
            Long studentId,
            Long quizId) {

        if (studentId == null || quizId == null) {
            throw new IllegalArgumentException(
                    "Student ID and quiz ID are required."
            );
        }
    }
}