package com.edusphere.controller;

import com.edusphere.model.Quiz;
import com.edusphere.model.Teacher;
import com.edusphere.service.QuizService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/teacher/courses/quizzes/delete")
public class DeleteQuizServlet extends HttpServlet {

    private final QuizService quizService = new QuizService();
    private final TeacherService teacherService =
            new TeacherService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "GET is not allowed."
        );
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

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

            String idParam =
                    request.getParameter("id");

            if (idParam == null || idParam.isBlank()) {
                throw new IllegalArgumentException(
                        "Quiz ID is required."
                );
            }

            Long quizId =
                    Long.parseLong(idParam);

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {
                throw new IllegalArgumentException(
                        "Teacher profile not found."
                );
            }

            Quiz quiz =
                    quizService.findById(quizId);

            if (quiz == null) {
                throw new IllegalArgumentException(
                        "Quiz not found."
                );
            }

            Long courseId =
                    quiz.getCourseId();

            quizService.deleteQuiz(
                    teacher.getId(),
                    quizId
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/quizzes?courseId="
                            + courseId
            );

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
                    "Unable to delete quiz.",
                    e
            );
        }
    }
}