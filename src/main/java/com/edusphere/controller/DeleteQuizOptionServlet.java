package com.edusphere.controller;

import com.edusphere.model.QuizOption;
import com.edusphere.model.Teacher;
import com.edusphere.service.QuizOptionService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/teacher/courses/quizzes/questions/options/delete")
public class DeleteQuizOptionServlet extends HttpServlet {

    private final QuizOptionService quizOptionService =
            new QuizOptionService();

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

            Long optionId =
                    Long.parseLong(
                            request.getParameter("id")
                    );

            Teacher teacher =
                    teacherService.findByUserId(userId);

            QuizOption option =
                    quizOptionService.findById(
                            optionId
                    );

            if (option == null) {
                throw new IllegalArgumentException(
                        "Option not found."
                );
            }

            Long questionId =
                    option.getQuestionId();

            quizOptionService.deleteOption(
                    teacher.getId(),
                    optionId
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/quizzes/questions/options?questionId="
                            + questionId
            );

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid option ID."
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
                    "Unable to delete option.",
                    e
            );
        }
    }
}