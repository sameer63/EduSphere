package com.edusphere.controller;

import com.edusphere.model.Quiz;
import com.edusphere.model.QuizAttempt;
import com.edusphere.model.Student;
import com.edusphere.service.LearningActivityService;
import com.edusphere.service.QuizAttemptService;
import com.edusphere.service.QuizService;
import com.edusphere.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;

@WebServlet("/student/courses/quizzes/submit")
public class SubmitQuizServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final QuizAttemptService quizAttemptService =
            new QuizAttemptService();
    private final LearningActivityService learningActivityService =
            new LearningActivityService();

    private final QuizService quizService =
            new QuizService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            /*
             * Get existing session.
             */
            HttpSession session =
                    request.getSession(false);

            if (session == null) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                return;
            }

            /*
             * Get logged-in user ID.
             */
            Long userId =
                    (Long) session.getAttribute("userId");

            if (userId == null) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                return;
            }

            /*
             * Get attempt ID.
             */
            String attemptIdParam =
                    request.getParameter("attemptId");

            if (attemptIdParam == null ||
                    attemptIdParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Attempt ID is required."
                );

                return;
            }

            Long attemptId;

            try {

                attemptId =
                        Long.parseLong(attemptIdParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid attempt ID."
                );

                return;
            }

            /*
             * Find student profile.
             */
            Student student =
                    studentService.findByUserId(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Student profile not found."
                                    )
                            );

            /*
             * Verify that the attempt exists.
             */
            QuizAttempt existingAttempt =
                    quizAttemptService.findById(attemptId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Quiz attempt not found."
                                    )
                            );

            /*
             * Security check:
             * attempt must belong to logged-in student.
             */
            if (!existingAttempt.getStudentId()
                    .equals(student.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to submit this attempt."
                );

                return;
            }

            /*
             * Submit the attempt.
             *
             * QuizAttemptService will:
             *
             * 1. Verify the attempt is not already submitted.
             * 2. Load all saved answers.
             * 3. Check each selected option.
             * 4. Add marks for correct answers.
             * 5. Calculate final score.
             * 6. Set submitted_at.
             */
            QuizAttempt submittedAttempt =
                    quizAttemptService.submitAttempt(
                            student.getId(),
                            attemptId
                    );
            try {
                Quiz quiz =
                        quizService.findById(submittedAttempt.getQuizId());

                learningActivityService.recordActivity(
                        userId,
                        student.getId(),
                        "QUIZ_SUBMITTED",
                        quiz.getCourseId(),
                        quiz.getId(),
                        Map.of(
                                "attemptId", submittedAttempt.getId(),
                                "attemptNumber", submittedAttempt.getAttemptNumber()
                        )
                );
            } catch (Exception e) {
                getServletContext().log(
                        "Unable to record quiz-submission activity.",
                        e
                );
            }

            /*
             * PRG pattern:
             *
             * POST → Redirect → GET
             *
             * Redirect to the result page.
             */
            response.sendRedirect(
                    request.getContextPath()
                            + "/student/courses/quizzes/result?id="
                            + submittedAttempt.getId()
            );

        } catch (SecurityException e) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );

        } catch (IllegalArgumentException |
                 IllegalStateException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to submit quiz.",
                    e
            );
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "GET is not allowed."
        );
    }
}