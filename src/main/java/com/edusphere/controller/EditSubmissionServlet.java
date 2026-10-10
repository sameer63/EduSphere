package com.edusphere.controller;

import com.edusphere.model.Assignment;
import com.edusphere.model.Course;
import com.edusphere.model.Student;
import com.edusphere.model.Submission;
import com.edusphere.service.AssignmentService;
import com.edusphere.service.CourseService;
import com.edusphere.service.StudentService;
import com.edusphere.service.SubmissionService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@WebServlet("/student/courses/assignments/submission/edit")
public class EditSubmissionServlet extends HttpServlet {

    private final SubmissionService submissionService =
            new SubmissionService();

    private final StudentService studentService =
            new StudentService();

    private final AssignmentService assignmentService =
            new AssignmentService();

    private final CourseService courseService =
            new CourseService();


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

            if (idParam == null || idParam.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Submission ID is required."
                );
                return;
            }

            Long submissionId;

            try {
                submissionId = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid submission ID."
                );
                return;
            }

            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);
            if(optionalStudent.isEmpty()){
                response.sendError(HttpServletResponse.SC_FORBIDDEN,"Student not found");
                return;
            }
            Student student=optionalStudent.get();

            if (student == null) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );
                return;
            }

            Submission submission =
                    submissionService.findById(submissionId);

            if (submission == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Submission not found."
                );
                return;
            }

            // Student can edit only their own submission.
            if (!submission.getStudentId()
                    .equals(student.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not authorised to edit this submission."
                );
                return;
            }

            if (submission.getMarks() != null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "A graded submission cannot be modified."
                );
                return;
            }

            Assignment assignment =
                    assignmentService.findById(
                            submission.getAssignmentId()
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

            boolean deadlinePassed =
                    assignment.getDeadline() != null &&
                            LocalDateTime.now()
                                    .isAfter(assignment.getDeadline());

            request.setAttribute(
                    "submission",
                    submission
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
                    deadlinePassed
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/edit-submission.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load edit submission page."
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

            String idParam =
                    request.getParameter("submissionId");

            String submissionText =
                    request.getParameter("submissionText");

            if (idParam == null || idParam.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Submission ID is required."
                );
                return;
            }

            Long submissionId;

            try {
                submissionId = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid submission ID."
                );
                return;
            }

            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);
            if(optionalStudent.isEmpty()){
                response.sendError(HttpServletResponse.SC_FORBIDDEN,"Student not found");
                return;
            }
            Student student=optionalStudent.get();

            if (student == null) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );
                return;
            }

            submissionService.updateSubmission(
                    student.getId(),
                    submissionId,
                    submissionText
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/student/courses/assignments/submission?id="
                            + submissionId
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            try {

                Long submissionId =
                        Long.parseLong(
                                request.getParameter("submissionId")
                        );

                Submission submission =
                        submissionService.findById(
                                submissionId
                        );

                if (submission == null) {
                    response.sendError(
                            HttpServletResponse.SC_NOT_FOUND,
                            "Submission not found."
                    );
                    return;
                }

                Assignment assignment =
                        assignmentService.findById(
                                submission.getAssignmentId()
                        );

                Course course =
                        courseService.findById(
                                assignment.getCourseId()
                        );

                request.setAttribute(
                        "submission",
                        submission
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
                                LocalDateTime.now()
                                        .isAfter(
                                                assignment.getDeadline()
                                        )
                );

                request.getRequestDispatcher(
                        "/WEB-INF/views/student/edit-submission.jsp"
                ).forward(request, response);

            } catch (Exception ignored) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        e.getMessage()
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to update submission."
            );
        }
    }
}