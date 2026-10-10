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
import java.util.List;

@WebServlet("/teacher/courses")
public class TeacherCoursesServlet extends HttpServlet {

    private TeacherService teacherService;
    private CourseService courseService;

    @Override
    public void init() {
        teacherService = new TeacherService();
        courseService = new CourseService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);

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

            /*
             * Do NOT get teacherId from the URL or form.
             * Determine it from the logged-in user.
             */
            Teacher teacher =
                    teacherService.findByUserId(userId);

            Long teacherId = teacher.getId();

            List<Course> courses =
                    courseService.findCoursesByTeacher(teacherId);

            request.setAttribute("courses", courses);

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/courses.jsp"
            ).forward(request, response);

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/courses.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            getServletContext().log(
                    "Error loading teacher courses",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load courses."
            );
        }
    }
}