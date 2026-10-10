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

@WebServlet("/student/courses/quizzes/start")
public class StartQuizServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final QuizAttemptService quizAttemptService =
            new QuizAttemptService();

    private final LearningActivityService learningActivityService =
            new LearningActivityService();
    private final QuizService quizService=new QuizService();

    @Override
    protected void doGet(
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
             * Get logged-in user.
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
             * Get quiz ID.
             */
            String quizIdParam =
                    request.getParameter("quizId");

            if (quizIdParam == null ||
                    quizIdParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Quiz ID is required."
                );

                return;
            }

            Long quizId;

            try {

                quizId =
                        Long.parseLong(quizIdParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid quiz ID."
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
             * Start or resume the quiz attempt.
             */
            QuizAttempt attempt =
                    quizAttemptService.startAttempt(
                            student.getId(),
                            quizId
                    );

            try {
                learningActivityService.recordActivity(
                        userId,
                        student.getId(),
                        "QUIZ_STARTED",
                        quizService.findById(quizId).getCourseId(),
                        quizId,
                        Map.of(
                                "attemptId", attempt.getId(),
                                "attemptNumber", attempt.getAttemptNumber()
                        )
                );
            } catch (Exception e) {
                getServletContext().log(
                        "Unable to record quiz-start activity.",
                        e
                );
            }

            /*
             * Redirect to the attempt page.
             *
             * We will create this servlet/page
             * in the next step.
             */
            response.sendRedirect(
                    request.getContextPath()
                            + "/student/courses/quizzes/attempt?id="
                            + attempt.getId()
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
                    "Unable to start quiz attempt.",
                    e
            );
        }
    }

}