package com.edusphere.controller;

import com.edusphere.dto.QuizAttemptQuestionResultDTO;
import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizAttempt;
import com.edusphere.model.QuizAttemptAnswer;
import com.edusphere.model.QuizOption;
import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Teacher;
import com.edusphere.service.CourseService;
import com.edusphere.service.QuizAttemptService;
import com.edusphere.service.QuizOptionService;
import com.edusphere.service.QuizQuestionService;
import com.edusphere.service.QuizService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/teacher/courses/quizzes/attempts/view")
public class TeacherQuizAttemptViewServlet extends HttpServlet {

    private final TeacherService teacherService =
            new TeacherService();

    private final QuizAttemptService quizAttemptService =
            new QuizAttemptService();

    private final QuizService quizService =
            new QuizService();

    private final CourseService courseService =
            new CourseService();

    private final QuizQuestionService quizQuestionService =
            new QuizQuestionService();

    private final QuizOptionService quizOptionService =
            new QuizOptionService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        try {

            HttpSession session =
                    request.getSession(false);

            if (session == null) {
                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

            Long userId =
                    (Long) session.getAttribute("userId");

            if (userId == null) {
                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {
                throw new IllegalArgumentException(
                        "Teacher profile not found."
                );
            }

            String attemptIdParameter =
                    request.getParameter("id");

            if (attemptIdParameter == null ||
                    attemptIdParameter.isBlank()) {

                throw new IllegalArgumentException(
                        "Attempt ID is required."
                );
            }

            Long attemptId;

            try {
                attemptId =
                        Long.parseLong(attemptIdParameter);

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "Invalid attempt ID."
                );
            }

            QuizAttempt attempt =
                    quizAttemptService.findById(attemptId)
                            .orElseThrow(()->new IllegalArgumentException("attempt not found"));

            if (attempt == null) {
                throw new IllegalArgumentException(
                        "Quiz attempt not found."
                );
            }

            Quiz quiz =
                    quizService.findById(
                            attempt.getQuizId()
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

            /*
             * Teacher ownership check
             */
            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to view this attempt."
                );
                return;
            }

            /*
             * Load questions
             */
            List<QuizQuestion> questions =
                    quizQuestionService.findByQuiz(
                            quiz.getId()
                    );

            /*
             * Load student's answers
             */
            List<QuizAttemptAnswer> answers =
                    quizAttemptService.findAnswers(
                            attempt.getStudentId(),
                            attempt.getId()
                    );

            /*
             * questionId -> selected optionId
             */
            Map<Long, Long> selectedOptions =
                    new HashMap<>();

            for (QuizAttemptAnswer answer : answers) {

                selectedOptions.put(
                        answer.getQuestionId(),
                        answer.getOptionId()
                );
            }

            /*
             * Create question result DTOs
             */
            List<QuizAttemptQuestionResultDTO> results =
                    new ArrayList<>();

            BigDecimal totalMarks =
                    BigDecimal.ZERO;

            BigDecimal calculatedScore =
                    BigDecimal.ZERO;

            for (QuizQuestion question : questions) {

                BigDecimal questionMarks =BigDecimal.valueOf( question.getMarks());

                totalMarks =
                        totalMarks.add(questionMarks);

                List<QuizOption> options =
                        quizOptionService.findByQuestion(
                                question.getId()
                        );

                QuizOption selectedOption = null;
                QuizOption correctOption = null;

                Long selectedOptionId =
                        selectedOptions.get(
                                question.getId()
                        );

                /*
                 * Find selected and correct options
                 */
                for (QuizOption option : options) {

                    if (selectedOptionId != null &&
                            option.getId()
                                    .equals(selectedOptionId)) {

                        selectedOption = option;
                    }

                    if (option.isCorrect()) {
                        correctOption = option;
                    }
                }

                boolean answered =
                        selectedOption != null;

                boolean correct =
                        answered
                                && correctOption != null
                                && selectedOption.getId()
                                .equals(correctOption.getId());

                BigDecimal marksAwarded =
                        correct
                                ? questionMarks
                                : BigDecimal.ZERO;

                calculatedScore =
                        calculatedScore.add(marksAwarded);

                QuizAttemptQuestionResultDTO result =
                        new QuizAttemptQuestionResultDTO(
                                question.getId(),
                                question.getQuestionText(),
                                questionMarks,
                                marksAwarded,
                                selectedOption != null
                                        ? selectedOption.getId()
                                        : null,
                                selectedOption != null
                                        ? selectedOption.getOptionText()
                                        : null,
                                correctOption != null
                                        ? correctOption.getId()
                                        : null,
                                correctOption != null
                                        ? correctOption.getOptionText()
                                        : null,
                                answered,
                                correct
                        );

                results.add(result);
            }

            /*
             * Use stored score for submitted attempts.
             *
             * For an in-progress attempt there may not
             * be a stored score, so calculatedScore is used
             * only for display if necessary.
             */
            BigDecimal score =
                    attempt.getScore();

            if (score == null) {
                score = calculatedScore;
            }

            BigDecimal percentage =
                    BigDecimal.ZERO;

            if (totalMarks.compareTo(
                    BigDecimal.ZERO
            ) > 0) {

                percentage =
                        score
                                .multiply(
                                        BigDecimal.valueOf(100)
                                )
                                .divide(
                                        totalMarks,
                                        2,
                                        RoundingMode.HALF_UP
                                );
            }

            request.setAttribute(
                    "teacher",
                    teacher
            );

            request.setAttribute(
                    "attempt",
                    attempt
            );

            request.setAttribute(
                    "quiz",
                    quiz
            );

            request.setAttribute(
                    "course",
                    course
            );

            request.setAttribute(
                    "results",
                    results
            );

            request.setAttribute(
                    "totalMarks",
                    totalMarks
            );

            request.setAttribute(
                    "score",
                    score
            );

            request.setAttribute(
                    "percentage",
                    percentage
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/quiz-attempt-view.jsp"
            ).forward(request, response);

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/error/error.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to load quiz attempt.",
                    e
            );
        }
    }
}