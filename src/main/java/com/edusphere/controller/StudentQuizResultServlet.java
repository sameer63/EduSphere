package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizAttempt;
import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Student;
import com.edusphere.service.CourseService;
import com.edusphere.service.QuizAttemptService;
import com.edusphere.service.QuizQuestionService;
import com.edusphere.service.QuizService;
import com.edusphere.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@WebServlet("/student/courses/quizzes/result")
public class StudentQuizResultServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final QuizAttemptService quizAttemptService =
            new QuizAttemptService();

    private final QuizService quizService =
            new QuizService();

    private final CourseService courseService =
            new CourseService();

    private final QuizQuestionService quizQuestionService =
            new QuizQuestionService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            HttpSession session =
                    request.getSession(false);

            if (session == null) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED
                );
                return;
            }

            Long userId =
                    (Long) session.getAttribute("userId");

            if (userId == null) {
                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED
                );
                return;
            }

            /*
             * Find logged-in student's profile.
             */
            Student student =
                    studentService.findByUserId(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Student profile not found."
                                    )
                            );

            /*
             * Get attempt ID.
             */
            String attemptIdParam =
                    request.getParameter("id");

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
             * Security:
             * the result belongs only to the student
             * who made the attempt.
             */
            if (!attempt.getStudentId()
                    .equals(student.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to view this result."
                );
                return;
            }

            /*
             * Result should only be displayed
             * after submission.
             */
            if (attempt.getSubmittedAt() == null) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "This quiz attempt has not been submitted yet."
                );
                return;
            }

            /*
             * Load quiz.
             */
            Quiz quiz =
                    quizService.findById(attempt.getQuizId());
            if(quiz==null){
                throw new IllegalArgumentException("Quiz not found");
            }

            /*
             * Load course.
             */
            Course course =
                    courseService.findById(quiz.getCourseId());


            /*
             * Calculate total marks from the questions.
             */
            List<QuizQuestion> questions =
                    quizQuestionService.findByQuiz(
                            quiz.getId()
                    );

            BigDecimal totalMarks =
                    BigDecimal.ZERO;

            for (QuizQuestion question : questions) {

                totalMarks =
                        totalMarks.add(
                                BigDecimal.valueOf(
                                        question.getMarks()
                                )
                        );
            }

            /*
             * Score stored in the database.
             */
            BigDecimal score =
                    attempt.getScore();

            if (score == null) {
                score = BigDecimal.ZERO;
            }

            /*
             * Calculate percentage.
             */
            BigDecimal percentage =
                    BigDecimal.ZERO;

            if (totalMarks.compareTo(BigDecimal.ZERO) > 0) {

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
                    "student",
                    student
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
                    "/WEB-INF/views/student/quiz-result.jsp"
            ).forward(request, response);

        } catch (SecurityException e) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to load quiz result.",
                    e
            );
        }
    }
}