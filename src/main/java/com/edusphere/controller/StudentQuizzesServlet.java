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
import java.util.List;
import java.util.Optional;

@WebServlet("/student/courses/quizzes")
public class StudentQuizzesServlet extends HttpServlet {

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
            HttpSession session = request.getSession(false);

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
             * Find the student profile belonging
             * to the logged-in user.
             */
            Student student =
                    studentService.findByUserId(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Student profile not found."
                                    )
                            );

            String courseIdParam =
                    request.getParameter("courseId");

            if (courseIdParam == null ||
                    courseIdParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Course ID is required."
                );
                return;
            }

            Long courseId;

            try {
                courseId =
                        Long.parseLong(courseIdParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid course ID."
                );
                return;
            }

            /*
             * Verify that the student is enrolled
             * in this course.
             */
            boolean enrolled =
                    enrolmentService.isStudentEnrolled(
                            student.getId(),
                            courseId
                    );

            if (!enrolled) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not enrolled in this course."
                );
                return;
            }

            /*
             * Load course.
             */
            Course course =
                    courseService.findById(courseId);

            /*
             * Load quizzes belonging to the course.
             */
            List<Quiz> quizzes =
                    quizService.findByCourse(courseId);

            request.setAttribute(
                    "course",
                    course
            );

            request.setAttribute(
                    "quizzes",
                    quizzes
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/quizzes.jsp"
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
                    "Unable to load student quizzes.",
                    e
            );
        }
    }
}