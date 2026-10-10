package com.edusphere.controller;

import com.edusphere.model.QuizAttempt;
import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Student;
import com.edusphere.service.QuizAttemptService;
import com.edusphere.service.QuizQuestionService;
import com.edusphere.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/student/courses/quizzes/answer")
public class AnswerQuizServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final QuizAttemptService quizAttemptService =
            new QuizAttemptService();

    private final QuizQuestionService quizQuestionService =
            new QuizQuestionService();

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
             * Load attempt.
             */
            QuizAttempt attempt =
                    quizAttemptService.findById(attemptId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Quiz attempt not found."
                                    )
                            );

            /*
             * Make sure this attempt belongs
             * to the logged-in student.
             */
            if (!attempt.getStudentId()
                    .equals(student.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to modify this attempt."
                );

                return;
            }

            /*
             * Do not allow changes after submission.
             */
            if (attempt.getSubmittedAt() != null) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "This quiz attempt has already been submitted."
                );

                return;
            }

            /*
             * Get all questions belonging to this quiz.
             */
            List<QuizQuestion> questions =
                    quizQuestionService.findByQuiz(
                            attempt.getQuizId()
                    );

            /*
             * Process every question.
             *
             * The JSP creates parameters like:
             *
             * question_1 = 5
             * question_2 = 8
             * question_3 = 12
             *
             * where:
             *
             * question_1 → question ID
             * 5           → selected option ID
             */
            for (QuizQuestion question : questions) {

                String parameterName =
                        "question_" + question.getId();

                String optionIdParam =
                        request.getParameter(parameterName);

                /*
                 * If the question was not answered,
                 * simply leave it unanswered.
                 */
                if (optionIdParam == null ||
                        optionIdParam.isBlank()) {

                    continue;
                }

                Long optionId;

                try {

                    optionId =
                            Long.parseLong(optionIdParam);

                } catch (NumberFormatException e) {

                    throw new IllegalArgumentException(
                            "Invalid option selected for question "
                                    + question.getId()
                    );
                }

                /*
                 * QuizAttemptService performs the important
                 * security validations:
                 *
                 * - attempt belongs to student
                 * - attempt isn't submitted
                 * - question belongs to quiz
                 * - option belongs to question
                 *
                 * It also updates an existing answer instead
                 * of creating a duplicate.
                 */
                quizAttemptService.answerQuestion(
                        student.getId(),
                        attemptId,
                        question.getId(),
                        optionId
                );
            }

            /*
             * PRG pattern:
             *
             * POST → Redirect → GET
             *
             * This prevents duplicate form submission
             * when the student refreshes the page.
             */
            response.sendRedirect(
                    request.getContextPath()
                            + "/student/courses/quizzes/attempt?id="
                            + attemptId
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
                    "Unable to save quiz answers.",
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