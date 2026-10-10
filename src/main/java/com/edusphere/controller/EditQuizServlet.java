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

@WebServlet("/teacher/courses/quizzes/edit")
public class EditQuizServlet extends HttpServlet {

    private final QuizService quizService = new QuizService();
    private final TeacherService teacherService = new TeacherService();
    private final CourseService courseService = new CourseService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {
            Long userId = (Long) session.getAttribute("userId");

            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isBlank()) {
                throw new IllegalArgumentException(
                        "Quiz ID is required."
                );
            }

            Long quizId = Long.parseLong(idParam);

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {
                throw new IllegalArgumentException(
                        "Teacher profile not found."
                );
            }

            Quiz quiz = quizService.findById(quizId);

            if (quiz == null) {
                throw new IllegalArgumentException(
                        "Quiz not found."
                );
            }

            Course course =
                    courseService.findById(quiz.getCourseId());

            if (course == null) {
                throw new IllegalArgumentException(
                        "Course not found."
                );
            }

            if (!course.getTeacherId().equals(teacher.getId())) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You do not own this quiz."
                );
                return;
            }

            request.setAttribute("quiz", quiz);
            request.setAttribute("course", course);

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/edit-quiz.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid quiz ID."
            );

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
                    "Unable to load edit quiz page.",
                    e
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {
            Long userId = (Long) session.getAttribute("userId");

            Long quizId = Long.parseLong(
                    request.getParameter("id")
            );

            String title = request.getParameter("title");
            String description =
                    request.getParameter("description");

            Integer maxAttempts = Integer.parseInt(
                    request.getParameter("maxAttempts")
            );

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {
                throw new IllegalArgumentException(
                        "Teacher profile not found."
                );
            }

            Quiz updatedQuiz =
                    quizService.updateQuiz(
                            teacher.getId(),
                            quizId,
                            title,
                            description,
                            maxAttempts
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/quizzes/view?id="
                            + updatedQuiz.getId()
            );

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Invalid quiz ID or maximum attempts."
            );

            try {
                Long quizId = Long.parseLong(
                        request.getParameter("id")
                );

                Quiz quiz = quizService.findById(quizId);

                if (quiz != null) {
                    request.setAttribute("quiz", quiz);

                    Course course =
                            courseService.findById(
                                    quiz.getCourseId()
                            );

                    request.setAttribute("course", course);
                }
            } catch (Exception ignored) {
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
                    "/WEB-INF/views/teacher/edit-quiz.jsp"
            ).forward(request, response);

        } catch (IllegalArgumentException e) {

            try {
                Long quizId = Long.parseLong(
                        request.getParameter("id")
                );

                Quiz quiz = quizService.findById(quizId);

                if (quiz != null) {
                    request.setAttribute("quiz", quiz);

                    Course course =
                            courseService.findById(
                                    quiz.getCourseId()
                            );

                    request.setAttribute("course", course);
                }
            } catch (Exception ignored) {
            }

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

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
                    "/WEB-INF/views/teacher/edit-quiz.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to update quiz.",
                    e
            );
        }
    }
}