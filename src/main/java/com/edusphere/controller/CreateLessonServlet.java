package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Teacher;
import com.edusphere.service.CourseService;
import com.edusphere.service.LessonService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/teacher/courses/lessons/create")
public class CreateLessonServlet extends HttpServlet {

    private final LessonService lessonService = new LessonService();
    private final TeacherService teacherService = new TeacherService();
    private final CourseService courseService = new CourseService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

            Long userId = (Long) session.getAttribute("userId");

            String courseIdParam = request.getParameter("courseId");

            if (courseIdParam == null || courseIdParam.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Course ID is required."
                );
                return;
            }

            Long courseId = Long.parseLong(courseIdParam);

            Teacher teacher = teacherService.findByUserId(userId);

            if (teacher == null) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            Course course = courseService.findById(courseId);

            if (course == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );
                return;
            }

            // Ownership check
            if (!course.getTeacherId().equals(teacher.getId())) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to manage this course."
                );
                return;
            }

            request.setAttribute("course", course);

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/create-lesson.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid course ID."
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/create-lesson.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong."
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession(false);

            if (session == null || session.getAttribute("userId") == null) {
                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

            Long userId = (Long) session.getAttribute("userId");

            String courseIdParam = request.getParameter("courseId");
            String title = request.getParameter("title");
            String content = request.getParameter("content");
            String positionParam = request.getParameter("position");

            if (courseIdParam == null || courseIdParam.isBlank()) {
                throw new IllegalArgumentException(
                        "Course ID is required."
                );
            }

            Long courseId = Long.parseLong(courseIdParam);

            Teacher teacher = teacherService.findByUserId(userId);

            if (teacher == null) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            Course course = courseService.findById(courseId);

            if (course == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );
                return;
            }

            // Ownership check
            if (!course.getTeacherId().equals(teacher.getId())) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to add lessons to this course."
                );
                return;
            }

            if (positionParam == null || positionParam.isBlank()) {
                throw new IllegalArgumentException(
                        "Position is required."
                );
            }

            Integer position = Integer.parseInt(positionParam);

            lessonService.createLesson(
                    teacher.getId(),
                    courseId,
                    title,
                    content,
                    position
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/lessons?courseId="
                            + courseId
            );

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Course ID and position must be valid numbers."
            );

            String courseId = request.getParameter("courseId");

            try {
                if (courseId != null && !courseId.isBlank()) {
                    Course course = courseService.findById(
                            Long.parseLong(courseId)
                    );
                    request.setAttribute("course", course);
                }
            } catch (Exception ignored) {
            }

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/create-lesson.jsp"
            ).forward(request, response);

        } catch (IllegalArgumentException e) {

            request.setAttribute("error", e.getMessage());

            String courseId = request.getParameter("courseId");

            try {
                if (courseId != null && !courseId.isBlank()) {
                    Course course = courseService.findById(
                            Long.parseLong(courseId)
                    );
                    request.setAttribute("course", course);
                }
            } catch (Exception ignored) {
            }

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/create-lesson.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong while creating the lesson."
            );
        }
    }
}