package com.edusphere.controller;

import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Teacher;
import com.edusphere.service.QuizQuestionService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/teacher/courses/quizzes/questions/delete")
public class DeleteQuizQuestionServlet extends HttpServlet {

    private final QuizQuestionService quizQuestionService =
            new QuizQuestionService();

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

            Long questionId =
                    Long.parseLong(
                            request.getParameter("id")
                    );

            Teacher teacher =
                    teacherService.findByUserId(userId);

            QuizQuestion question =
                    quizQuestionService.findById(
                            questionId
                    );

            if (question == null) {
                throw new IllegalArgumentException(
                        "Question not found."
                );
            }

            Long quizId =
                    question.getQuizId();

            quizQuestionService.deleteQuestion(
                    teacher.getId(),
                    questionId
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/quizzes/questions?quizId="
                            + quizId
            );

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid question ID."
            );

        } catch (SecurityException e) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
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
                    "Unable to delete question.",
                    e
            );
        }
    }
}