package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Teacher;
import com.edusphere.service.CourseService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/teacher/courses/status")
public class UpdateCourseStatusServlet extends HttpServlet {

    private TeacherService teacherService;
    private CourseService courseService;

    @Override
    public void init() {

        teacherService = new TeacherService();
        courseService = new CourseService();
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

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

            String courseIdParameter =
                    request.getParameter("id");

            String status =
                    request.getParameter("status");

            if (courseIdParameter == null ||
                    courseIdParameter.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Course ID is required."
                );

                return;
            }

            Long courseId =
                    Long.parseLong(courseIdParameter);

            Teacher teacher =
                    teacherService.findByUserId(userId);

            Course course =
                    courseService.updateCourseStatus(
                            courseId,
                            teacher.getId(),
                            status
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/view?id="
                            + course.getId()
            );

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid course ID."
            );

        } catch (IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );

        } catch (Exception e) {

            getServletContext().log(
                    "Error updating course status",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update course status."
            );
        }
    }
}