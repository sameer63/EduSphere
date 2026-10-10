package com.edusphere.controller;

import com.edusphere.model.Submission;
import com.edusphere.model.Teacher;
import com.edusphere.service.SubmissionService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/teacher/courses/assignments/submissions/grade")
public class GradeSubmissionServlet extends HttpServlet {

    private final SubmissionService submissionService =
            new SubmissionService();

    private final TeacherService teacherService =
            new TeacherService();


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

            String submissionParam =
                    request.getParameter("submissionId");

            String marksParam =
                    request.getParameter("marks");

            String feedback =
                    request.getParameter("feedback");

            if (submissionParam == null ||
                    submissionParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Submission ID is required."
                );
                return;
            }

            if (marksParam == null ||
                    marksParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Marks are required."
                );
                return;
            }

            Long submissionId;

            try {

                submissionId =
                        Long.parseLong(submissionParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid submission ID."
                );
                return;
            }

            BigDecimal marks;

            try {

                marks =
                        new BigDecimal(marksParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid marks."
                );
                return;
            }

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Teacher profile not found."
                );
                return;
            }

            submissionService.gradeSubmission(
                    teacher.getId(),
                    submissionId,
                    marks,
                    feedback
            );

            /*
             * PRG pattern:
             *
             * POST → Redirect → GET
             */
            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/assignments/submissions/view?id="
                            + submissionId
            );

        } catch (IllegalArgumentException e) {

            String submissionIdParam =
                    request.getParameter("submissionId");

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            request.setAttribute(
                    "submittedMarks",
                    request.getParameter("marks")
            );

            request.setAttribute(
                    "submittedFeedback",
                    request.getParameter("feedback")
            );

            try {

                Long submissionId =
                        Long.parseLong(submissionIdParam);

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

                /*
                 * The JSP needs submission, assignment and course.
                 *
                 * The GET servlet normally prepares these objects,
                 * but POST validation errors bypass that servlet.
                 */
                com.edusphere.model.Assignment assignment =
                        new com.edusphere.service.AssignmentService()
                                .findById(
                                        submission.getAssignmentId()
                                );

                com.edusphere.model.Course course =
                        new com.edusphere.service.CourseService()
                                .findById(
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

                request.getRequestDispatcher(
                        "/WEB-INF/views/teacher/submission-view.jsp"
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
                    "Unable to grade submission."
            );
        }
    }


    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "GET is not allowed."
        );
    }
}