package com.edusphere.controller;

import com.edusphere.model.Assignment;
import com.edusphere.model.Course;
import com.edusphere.model.Teacher;
import com.edusphere.service.AssignmentService;
import com.edusphere.service.CourseService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet("/teacher/courses/assignments/create")
public class CreateAssignmentServlet extends HttpServlet {

    private final AssignmentService assignmentService =
            new AssignmentService();

    private final TeacherService teacherService =
            new TeacherService();

    private final CourseService courseService =
            new CourseService();

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {

            HttpSession session =
                    request.getSession(false);

            if (session == null ||
                    session.getAttribute("userId") == null) {

                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

            Long userId =
                    (Long) session.getAttribute("userId");

            String courseIdParam =
                    request.getParameter("courseId");

            if (courseIdParam == null ||
                    courseIdParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Course ID is required."
                );
                return;
            }

            Long courseId =
                    Long.parseLong(courseIdParam);

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            Course course =
                    courseService.findById(courseId);

            if (course == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );
                return;
            }

            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to create assignments for this course."
                );
                return;
            }

            request.setAttribute("course", course);

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/create-assignment.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid course ID."
            );

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

        Course course = null;

        try {

            HttpSession session =
                    request.getSession(false);

            if (session == null ||
                    session.getAttribute("userId") == null) {

                response.sendRedirect(
                        request.getContextPath() + "/login"
                );
                return;
            }

            Long userId =
                    (Long) session.getAttribute("userId");

            String courseIdParam =
                    request.getParameter("courseId");

            String title =
                    request.getParameter("title");

            String description =
                    request.getParameter("description");

            String deadlineParam =
                    request.getParameter("deadline");

            String maxMarksParam =
                    request.getParameter("maxMarks");

            if (courseIdParam == null ||
                    courseIdParam.isBlank()) {

                throw new IllegalArgumentException(
                        "Course ID is required."
                );
            }

            Long courseId =
                    Long.parseLong(courseIdParam);

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            course =
                    courseService.findById(courseId);

            if (course == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );
                return;
            }

            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to create assignments for this course."
                );
                return;
            }

            if (maxMarksParam == null ||
                    maxMarksParam.isBlank()) {

                throw new IllegalArgumentException(
                        "Maximum marks are required."
                );
            }

            BigDecimal maxMarks =
                    new BigDecimal(maxMarksParam);

            LocalDateTime deadline = null;

            if (deadlineParam != null &&
                    !deadlineParam.isBlank()) {

                deadline =
                        LocalDateTime.parse(
                                deadlineParam,
                                DATE_TIME_FORMATTER
                        );
            }

            Assignment assignment =
                    assignmentService.createAssignment(
                            teacher.getId(),
                            courseId,
                            title,
                            description,
                            deadline,
                            maxMarks
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/assignments/view?id="
                            + assignment.getId()
            );

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Course ID or maximum marks is invalid."
            );

            request.setAttribute("course", course);

            forwardToForm(request, response);

        } catch (java.time.format.DateTimeParseException e) {

            request.setAttribute(
                    "error",
                    "Invalid deadline format."
            );

            request.setAttribute("course", course);

            forwardToForm(request, response);

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            request.setAttribute("course", course);

            forwardToForm(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong while creating the assignment."
            );
        }
    }

    private void forwardToForm(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher(
                "/WEB-INF/views/teacher/create-assignment.jsp"
        ).forward(request, response);
    }
}