package com.edusphere.controller;

import com.edusphere.model.Enrolment;
import com.edusphere.model.Student;
import com.edusphere.service.EnrolmentService;
import com.edusphere.service.StudentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/student/courses/enrol")
public class EnrolCourseServlet extends HttpServlet {

    private StudentService studentService;
    private EnrolmentService enrolmentService;

    @Override
    public void init() {

        studentService = new StudentService();
        enrolmentService = new EnrolmentService();
    }

    @Override
    protected void doPost(
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
             * Resolve the actual student from the
             * authenticated user's ID.
             *
             * We NEVER trust studentId from the browser.
             */
            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);

            Student student=optionalStudent.get();

            Enrolment enrolment =
                    enrolmentService.enrolStudent(
                            student.getId(),
                            courseId
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/student/enrolments"
            );

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid course ID."
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/error/error.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            getServletContext().log(
                    "Error enrolling student",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to enrol in course."
            );
        }
    }
}