package com.edusphere.controller;

import com.edusphere.model.Assignment;
import com.edusphere.model.Course;
import com.edusphere.model.Student;
import com.edusphere.model.Submission;
import com.edusphere.service.AssignmentService;
import com.edusphere.service.CourseService;
import com.edusphere.service.EnrolmentService;
import com.edusphere.service.StudentService;
import com.edusphere.service.SubmissionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/student/courses/assignments/submit")
public class CreateSubmissionServlet extends HttpServlet {

    private final SubmissionService submissionService =
            new SubmissionService();

    private final StudentService studentService =
            new StudentService();

    private final AssignmentService assignmentService =
            new AssignmentService();

    private final CourseService courseService =
            new CourseService();

    private final EnrolmentService enrolmentService =
            new EnrolmentService();


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

            Long assignmentId;

            try {
                assignmentId =
                        Long.parseLong(idParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid assignment ID."
                );
                return;
            }

            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);

            if (optionalStudent.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );
                return;
            }

            Student student = optionalStudent.get();

            if (student == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
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

            boolean enrolled =
                    enrolmentService.isStudentEnrolled(
                            student.getId(),
                            course.getId()
                    );

            if (!enrolled) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not enrolled in this course."
                );
                return;
            }

            /*
             * Check whether the student has already submitted.
             */
            Submission existing =
                    submissionService
                            .findByAssignmentAndStudent(
                                    assignmentId,
                                    student.getId()
                            );

            if (existing != null) {

                /*
                 * Already submitted.
                 * Send the student to the submission view
                 * instead of creating another submission.
                 */
                response.sendRedirect(
                        request.getContextPath()
                                + "/student/courses/assignments/submission?id="
                                + existing.getId()
                );
                return;
            }

            /*
             * Deadline check.
             *
             * We perform the actual authoritative check
             * again inside SubmissionService when POST
             * happens.
             *
             * This check is only for displaying the page.
             */
            if (assignment.getDeadline() != null &&
                    java.time.LocalDateTime.now()
                            .isAfter(assignment.getDeadline())) {

                request.setAttribute(
                        "deadlinePassed",
                        true
                );
            } else {

                request.setAttribute(
                        "deadlinePassed",
                        false
                );
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
                    "/WEB-INF/views/student/submit-assignment.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load submission page."
            );
        }
    }


    @Override
    protected void doPost(HttpServletRequest request,
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

            String submissionText =
                    request.getParameter("submissionText");

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

            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);

            if (optionalStudent.isEmpty()) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );
                return;
            }

            Student student = optionalStudent.get();

            if (student == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );
                return;
            }

            submissionService.createSubmission(
                    student.getId(),
                    assignmentId,
                    submissionText
            );

            /*
             * Redirect to assignment list after success.
             *
             * PRG pattern:
             * POST → Redirect → GET
             */
            Assignment assignment =
                    assignmentService.findById(
                            assignmentId
                    );

            if (assignment == null) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/student/enrolments"
                );
                return;
            }

            response.sendRedirect(
                    request.getContextPath()
                            + "/student/courses/assignments/view?id="
                            + assignmentId
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            /*
             * Reload the assignment page so the user
             * can correct the submission.
             */
            try {

                String assignmentParam =
                        request.getParameter("assignmentId");

                Long assignmentId =
                        Long.parseLong(assignmentParam);

                Assignment assignment =
                        assignmentService.findById(
                                assignmentId
                        );

                if (assignment != null) {

                    Course course =
                            courseService.findById(
                                    assignment.getCourseId()
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
                            "deadlinePassed",
                            assignment.getDeadline() != null &&
                                    java.time.LocalDateTime.now()
                                            .isAfter(
                                                    assignment.getDeadline()
                                            )
                    );

                    request.getRequestDispatcher(
                            "/WEB-INF/views/student/submit-assignment.jsp"
                    ).forward(request, response);

                    return;
                }

            } catch (Exception ignored) {
                // Fall through to server error.
            }

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to submit assignment."
            );
        }
    }
}