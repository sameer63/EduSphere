package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizAttempt;
import com.edusphere.model.Student;
import com.edusphere.service.CourseService;
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
import java.util.List;

@WebServlet("/student/courses/quizzes/attempts")
public class StudentQuizAttemptsServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final QuizService quizService =
            new QuizService();

    private final CourseService courseService =
            new CourseService();

    private final QuizAttemptService quizAttemptService =
            new QuizAttemptService();

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

            Student student =
                    studentService.findByUserId(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Student profile not found."
                                    )
                            );

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

            Quiz quiz =
                    quizService.findById(quizId);
            if(quiz==null){
                throw new IllegalArgumentException("Quiz not found");
            }

            Course course =
                    courseService.findById(quiz.getCourseId());

            /*
             * QuizAttemptService validates the student
             * and loads the student's attempts.
             */
            List<QuizAttempt> attempts =
                    quizAttemptService.findByQuizAndStudent(
                            quizId,
                            student.getId()
                    );

            request.setAttribute(
                    "student",
                    student
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
                    "attempts",
                    attempts
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/quiz-attempts.jsp"
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
                    "Unable to load quiz attempts.",
                    e
            );
        }
    }
}