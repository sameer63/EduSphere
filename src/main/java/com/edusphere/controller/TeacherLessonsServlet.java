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
import java.util.List;

@WebServlet("/teacher/courses/lessons")
public class TeacherLessonsServlet extends HttpServlet {

    private TeacherService teacherService;
    private CourseService courseService;
    private LessonService lessonService;

    @Override
    public void init() {

        teacherService = new TeacherService();
        courseService = new CourseService();
        lessonService = new LessonService();
    }

    @Override
    protected void doGet(
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
                    request.getParameter("courseId");

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

            /*
             * Resolve the logged-in teacher.
             */
            Teacher teacher =
                    teacherService.findByUserId(userId);

            /*
             * Get the course.
             */
            Course course =
                    courseService.findById(courseId);

            /*
             * Ownership check.
             */
            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to manage lessons for this course."
                );

                return;
            }

            /*
             * Get lessons for this course.
             */
            List<Lesson> lessons =
                    lessonService.findByCourse(courseId);

            request.setAttribute(
                    "course",
                    course
            );

            request.setAttribute(
                    "lessons",
                    lessons
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/lessons.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid course ID."
            );

        } catch (IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    e.getMessage()
            );

        } catch (Exception e) {

            getServletContext().log(
                    "Error loading teacher lessons",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load lessons."
            );
        }
    }
}