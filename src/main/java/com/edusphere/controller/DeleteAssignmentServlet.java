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

@WebServlet("/teacher/courses/assignments/delete")
public class DeleteAssignmentServlet extends HttpServlet {

    private final AssignmentService assignmentService =
            new AssignmentService();

    private final TeacherService teacherService =
            new TeacherService();

    private final CourseService courseService =
            new CourseService();

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {

            // Existing session
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

            // Assignment ID
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

            // Teacher
            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );

                return;
            }

            // Assignment
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

            // Course
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
                        "You are not allowed to delete this assignment."
                );

                return;
            }

            // Delete
            assignmentService.deleteAssignment(
                    teacher.getId(),
                    assignmentId
            );

            // Return to assignment list
            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/assignments?courseId="
                            + course.getId()
            );

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid assignment ID."
            );

        } catch (IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong while deleting the assignment."
            );
        }
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // Deletion must never happen through GET
        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "Assignment deletion must use POST."
        );
    }
}