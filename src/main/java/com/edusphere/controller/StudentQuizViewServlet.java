package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.Student;
import com.edusphere.service.CourseService;
import com.edusphere.service.EnrolmentService;
import com.edusphere.service.QuizService;
import com.edusphere.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/student/courses/quizzes/view")
public class StudentQuizViewServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final CourseService courseService =
            new CourseService();

    private final QuizService quizService =
            new QuizService();

    private final EnrolmentService enrolmentService =
            new EnrolmentService();

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
             * Find the student profile.
             */
            Student student =
                    studentService.findByUserId(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Student profile not found."
                                    )
                            );

            String quizIdParam =
                    request.getParameter("id");

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
             * Load quiz.
             */
            Quiz quiz =
                    quizService.findById(quizId);
            if(quiz==null){
                throw new IllegalArgumentException("Quiz not found");
            }

            /*
             * Load course belonging to the quiz.
             */
            Course course =
                    courseService.findById(quiz.getCourseId());

            /*
             * Verify that the student is enrolled
             * in the course containing this quiz.
             */
            boolean enrolled =
                    enrolmentService.isStudentEnrolled(
                            student.getId(),
                            course.getId()
                    );

            if (!enrolled) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not enrolled in this course."
                );
                return;
            }

            request.setAttribute(
                    "quiz",
                    quiz
            );

            request.setAttribute(
                    "course",
                    course
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/quiz-view.jsp"
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
                    "Unable to load quiz.",
                    e
            );
        }
    }
}