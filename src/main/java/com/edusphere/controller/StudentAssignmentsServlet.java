package com.edusphere.controller;

import com.edusphere.model.Assignment;
import com.edusphere.model.Course;
import com.edusphere.model.Student;
import com.edusphere.service.AssignmentService;
import com.edusphere.service.CourseService;
import com.edusphere.service.EnrolmentService;
import com.edusphere.service.StudentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/student/courses/assignments")
public class StudentAssignmentsServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final CourseService courseService =
            new CourseService();

    private final EnrolmentService enrolmentService =
            new EnrolmentService();

    private final AssignmentService assignmentService =
            new AssignmentService();

    @Override
    protected void doGet(HttpServletRequest request,
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

            // Course ID
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

            // Find student
            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);
            Student student=optionalStudent.get();

            if (student == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );

                return;
            }

            // Find course
            Course course =
                    courseService.findById(courseId);

            if (course == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );

                return;
            }

            // Verify enrolment
            boolean enrolled =
                    enrolmentService.isStudentEnrolled(
                            student.getId(),
                            courseId
                    );

            if (!enrolled) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You must be enrolled in this course to access its assignments."
                );

                return;
            }

            // Get assignments
            List<Assignment> assignments =
                    assignmentService.findByCourse(
                            courseId
                    );

            request.setAttribute(
                    "course",
                    course
            );

            request.setAttribute(
                    "assignments",
                    assignments
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/assignments.jsp"
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
                    "Something went wrong while loading assignments."
            );
        }
    }
}