package com.edusphere.controller;

import com.edusphere.model.Assignment;
import com.edusphere.model.Course;
import com.edusphere.model.Submission;
import com.edusphere.model.Teacher;
import com.edusphere.service.AssignmentService;
import com.edusphere.service.CourseService;
import com.edusphere.service.SubmissionService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet("/teacher/courses/assignments/submissions")
public class TeacherSubmissionsServlet extends HttpServlet {

    private final SubmissionService submissionService =
            new SubmissionService();

    private final AssignmentService assignmentService =
            new AssignmentService();

    private final CourseService courseService =
            new CourseService();

    private final TeacherService teacherService =
            new TeacherService();


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {

            Long userId =
                    (Long) session.getAttribute("userId");

            String assignmentParam =
                    request.getParameter("assignmentId");

            if (assignmentParam == null ||
                    assignmentParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Assignment ID is required."
                );
                return;
            }

            Long assignmentId;

            try {

                assignmentId =
                        Long.parseLong(assignmentParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid assignment ID."
                );
                return;
            }

            /*
             * Find teacher profile from logged-in user.
             */
            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            /*
             * Find assignment.
             */
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

            /*
             * Find course.
             */
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

            /*
             * SECURITY:
             *
             * The logged-in teacher must own
             * the course.
             */
            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not authorised to view these submissions."
                );
                return;
            }

            /*
             * Get all submissions for this assignment.
             */
            List<Submission> submissions =
                    submissionService.findByAssignment(
                            assignmentId
                    );

            request.setAttribute(
                    "assignment",
                    assignment
            );

            request.setAttribute(
                    "course",
                    course
            );

            request.setAttribute(
                    "submissions",
                    submissions
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/submissions.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load submissions."
            );
        }
    }
}