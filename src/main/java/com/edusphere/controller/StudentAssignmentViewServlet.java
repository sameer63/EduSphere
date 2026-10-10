package com.edusphere.controller;

import com.edusphere.model.Assignment;
import com.edusphere.model.Course;
import com.edusphere.model.Student;
import com.edusphere.model.Submission;
import com.edusphere.service.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/student/courses/assignments/view")
public class StudentAssignmentViewServlet extends HttpServlet {

    private final AssignmentService assignmentService = new AssignmentService();
    private final CourseService courseService = new CourseService();
    private final StudentService studentService = new StudentService();
    private final EnrolmentService enrolmentService = new EnrolmentService();
    private final SubmissionService submissionService = new SubmissionService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {
            Long userId = (Long) session.getAttribute("userId");

            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isBlank()) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Assignment ID is required."
                );
                return;
            }

            Long assignmentId;

            try {
                assignmentId = Long.parseLong(idParam);
            } catch (NumberFormatException e) {
                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid assignment ID."
                );
                return;
            }

            // Find student profile
            Optional<Student> optionalStudent = studentService.findByUserId(userId);
            Student student=optionalStudent.get();

            if (student == null) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );
                return;
            }

            // Find assignment
            Assignment assignment =
                    assignmentService.findById(assignmentId);

            if (assignment == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Assignment not found."
                );
                return;
            }

            // Find course to which assignment belongs
            Course course =
                    courseService.findById(assignment.getCourseId());

            if (course == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );
                return;
            }

            // Student must be enrolled in the course
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

            Submission submission =
                    submissionService.findByAssignmentAndStudent(
                            assignmentId,
                            student.getId()
                    );

            request.setAttribute("submission", submission);
            request.setAttribute("assignment", assignment);
            request.setAttribute("course", course);

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/assignment-view.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load assignment."
            );
        }
    }
}