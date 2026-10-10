
package com.edusphere.controller;

import com.edusphere.model.LearningActivity;
import com.edusphere.model.Student;
import com.edusphere.service.LearningActivityService;
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

@WebServlet("/student/activity")
public class StudentActivityServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final LearningActivityService activityService =
            new LearningActivityService();

    @Override
    protected void doGet(
            HttpServletRequest request,
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

        Object userIdAttribute = session.getAttribute("userId");

        if (!(userIdAttribute instanceof Long userId)) {
            response.sendError(
                    HttpServletResponse.SC_UNAUTHORIZED
            );
            return;
        }

        try {
            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);

            if (optionalStudent.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );
                return;
            }

            Student student = optionalStudent.get();

            List<LearningActivity> activities =
                    activityService.findRecentByStudent(
                            student.getId(),
                            50
                    );

            request.setAttribute("activities", activities);

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/activity.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            throw new ServletException(
                    "Unable to load student activity.",
                    e
            );
        }
    }
}
