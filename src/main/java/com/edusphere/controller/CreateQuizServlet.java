package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.Teacher;
import com.edusphere.service.CourseService;
import com.edusphere.service.QuizService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/teacher/courses/quizzes/create")
public class CreateQuizServlet extends HttpServlet {

    private final QuizService quizService =
            new QuizService();

    private final CourseService courseService =
            new CourseService();

    private final TeacherService teacherService =
            new TeacherService();


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {

            Long userId =
                    (Long) session.getAttribute("userId");

            String courseParam =
                    request.getParameter("courseId");

            if (courseParam == null ||
                    courseParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Course ID is required."
                );
                return;
            }

            Long courseId;

            try {

                courseId =
                        Long.parseLong(courseParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid course ID."
                );
                return;
            }

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            Course course =
                    courseService.findById(courseId);

            if (course == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );
                return;
            }

            /*
             * Security:
             * Only course owner can create quizzes.
             */
            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not authorised to create a quiz for this course."
                );
                return;
            }

            request.setAttribute(
                    "course",
                    course
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/create-quiz.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load quiz creation page."
            );
        }
    }


    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {

            Long userId =
                    (Long) session.getAttribute("userId");

            String courseParam =
                    request.getParameter("courseId");

            String title =
                    request.getParameter("title");

            String description =
                    request.getParameter("description");

            String maxAttemptsParam =
                    request.getParameter("maxAttempts");

            if (courseParam == null ||
                    courseParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Course ID is required."
                );
                return;
            }

            Long courseId;

            try {

                courseId =
                        Long.parseLong(courseParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid course ID."
                );
                return;
            }

            Integer maxAttempts;

            try {

                maxAttempts =
                        Integer.parseInt(maxAttemptsParam);

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "Maximum attempts must be a valid number."
                );
            }

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            Quiz quiz =
                    quizService.createQuiz(
                            teacher.getId(),
                            courseId,
                            title,
                            description,
                            maxAttempts
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/quizzes/view?id="
                            + quiz.getId()
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            try {

                String courseParam =
                        request.getParameter("courseId");

                Long courseId =
                        Long.parseLong(courseParam);

                Course course =
                        courseService.findById(courseId);

                request.setAttribute(
                        "course",
                        course
                );

            } catch (Exception ignored) {
                // Course loading failed.
            }

            request.setAttribute(
                    "title",
                    request.getParameter("title")
            );

            request.setAttribute(
                    "description",
                    request.getParameter("description")
            );

            request.setAttribute(
                    "maxAttempts",
                    request.getParameter("maxAttempts")
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/create-quiz.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to create quiz."
            );
        }
    }
}