package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.service.CourseService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@WebServlet("/student/courses")
public class StudentCoursesServlet extends HttpServlet {

    private CourseService courseService;

    @Override
    public void init() {
        courseService = new CourseService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            List<Course> courses =
                    courseService.findPublishedCourses();

            request.setAttribute(
                    "courses",
                    courses
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/courses.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            getServletContext().log(
                    "Error loading published courses",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load courses."
            );
        }
    }
}