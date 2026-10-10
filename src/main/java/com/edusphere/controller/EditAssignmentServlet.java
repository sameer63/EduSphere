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

@WebServlet("/teacher/courses/assignments/edit")
public class EditAssignmentServlet extends HttpServlet {

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

            String idParam =
                    request.getParameter("id");

            if (idParam == null ||
                    idParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Assignment ID is required."
                );
                return;
            }

            Long assignmentId =
                    Long.parseLong(idParam);

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            Assignment assignment =
                    assignmentService.findById(
                            assignmentId
                    );

            if (assignment == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Assignment not found."
                );
                return;
            }

            Course course =
                    courseService.findById(
                            assignment.getCourseId()
                    );

            if (course == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );
                return;
            }

            // Ownership check
            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to edit this assignment."
                );
                return;
            }

            request.setAttribute(
                    "assignment",
                    assignment
            );

            request.setAttribute(
                    "course",
                    course
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/edit-assignment.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid assignment ID."
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong while loading the assignment."
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        Assignment assignment = null;
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

            String idParam =
                    request.getParameter("id");

            if (idParam == null ||
                    idParam.isBlank()) {

                throw new IllegalArgumentException(
                        "Assignment ID is required."
                );
            }

            Long assignmentId =
                    Long.parseLong(idParam);

            String title =
                    request.getParameter("title");

            String description =
                    request.getParameter("description");

            String deadlineParam =
                    request.getParameter("deadline");

            String maxMarksParam =
                    request.getParameter("maxMarks");

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            assignment =
                    assignmentService.findById(
                            assignmentId
                    );

            if (assignment == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Assignment not found."
                );
                return;
            }

            course =
                    courseService.findById(
                            assignment.getCourseId()
                    );

            if (course == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );
                return;
            }

            // Ownership check
            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to edit this assignment."
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

            assignmentService.updateAssignment(
                    teacher.getId(),
                    assignmentId,
                    title,
                    description,
                    deadline,
                    maxMarks
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/assignments/view?id="
                            + assignmentId
            );

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Assignment ID or maximum marks is invalid."
            );

            reloadForm(
                    request,
                    response,
                    assignment,
                    course
            );

        } catch (java.time.format.DateTimeParseException e) {

            request.setAttribute(
                    "error",
                    "Invalid deadline format."
            );

            reloadForm(
                    request,
                    response,
                    assignment,
                    course
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            reloadForm(
                    request,
                    response,
                    assignment,
                    course
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong while updating the assignment."
            );
        }
    }

    private void reloadForm(
            HttpServletRequest request,
            HttpServletResponse response,
            Assignment assignment,
            Course course)
            throws ServletException, IOException {

        if (assignment != null) {
            request.setAttribute(
                    "assignment",
                    assignment
            );
        }

        if (course != null) {
            request.setAttribute(
                    "course",
                    course
            );
        }

        request.getRequestDispatcher(
                "/WEB-INF/views/teacher/edit-assignment.jsp"
        ).forward(request, response);
    }
}