package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Lesson;
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

@WebServlet("/teacher/courses/lessons/view")
public class ViewLessonServlet extends HttpServlet {

    private final LessonService lessonService = new LessonService();
    private final TeacherService teacherService = new TeacherService();
    private final CourseService courseService = new CourseService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Get existing session
            HttpSession session = request.getSession(false);

            if (session == null ||
                    session.getAttribute("userId") == null) {

                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

            // Logged-in user
            Long userId = (Long) session.getAttribute("userId");

            // Get lesson ID
            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Lesson ID is required."
                );
                return;
            }

            Long lessonId = Long.parseLong(idParam);

            // Find teacher profile
            Teacher teacher = teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            // Find lesson
            Lesson lesson = lessonService.findById(lessonId);

            if (lesson == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Lesson not found."
                );
                return;
            }

            // Find course
            Course course = courseService.findById(
                    lesson.getCourseId()
            );

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
                        "You are not allowed to view this lesson."
                );
                return;
            }

            // Send data to JSP
            request.setAttribute("lesson", lesson);
            request.setAttribute("course", course);

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/view-lesson.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid lesson ID."
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong while loading the lesson."
            );
        }
    }
}