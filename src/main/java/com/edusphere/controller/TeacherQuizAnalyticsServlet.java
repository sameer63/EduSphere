package com.edusphere.controller;

import com.edusphere.dto.QuizAnalyticsDTO;
import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Teacher;
import com.edusphere.service.CourseService;
import com.edusphere.service.QuizAttemptService;
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
import java.util.List;

@WebServlet("/teacher/courses/quizzes/analytics")
public class TeacherQuizAnalyticsServlet extends HttpServlet {

    private final TeacherService teacherService =
            new TeacherService();

    private final QuizService quizService =
            new QuizService();

    private final CourseService courseService =
            new CourseService();

    private final QuizQuestionService quizQuestionService =
            new QuizQuestionService();

    private final QuizAttemptService quizAttemptService =
            new QuizAttemptService();

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

            String quizIdParameter =
                    request.getParameter("quizId");

            if (quizIdParameter == null ||
                    quizIdParameter.isBlank()) {

                throw new IllegalArgumentException(
                        "Quiz ID is required."
                );
            }

            Long quizId;

            try {

                quizId =
                        Long.parseLong(quizIdParameter);

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "Invalid quiz ID."
                );
            }

            Quiz quiz =
                    quizService.findById(quizId);

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
                        "You are not allowed to view analytics for this quiz."
                );

                return;
            }

            /*
             * Calculate total marks from questions.
             */
            List<QuizQuestion> questions =
                    quizQuestionService.findByQuiz(
                            quiz.getId()
                    );

            BigDecimal totalMarks =
                    BigDecimal.ZERO;

            for (QuizQuestion question : questions) {

                totalMarks =
                        totalMarks.add(BigDecimal.valueOf( question.getMarks()));
            }

            QuizAnalyticsDTO analytics =
                    quizAttemptService.getQuizAnalytics(
                            quizId,
                            totalMarks
                    );

            request.setAttribute(
                    "teacher",
                    teacher
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
                    "analytics",
                    analytics
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/quiz-analytics.jsp"
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
                    "Unable to load quiz analytics.",
                    e
            );
        }
    }
}