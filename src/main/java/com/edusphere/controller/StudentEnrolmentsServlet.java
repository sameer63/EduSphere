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
import java.util.List;
import java.util.Optional;

@WebServlet("/student/enrolments")
public class StudentEnrolmentsServlet extends HttpServlet {

    private StudentService studentService;
    private EnrolmentService enrolmentService;

    @Override
    public void init() {
        studentService = new StudentService();
        enrolmentService = new EnrolmentService();
    }

    @Override
    protected void doGet(
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

            /*
             * Resolve the logged-in user's student profile.
             */
            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);
            Student student=optionalStudent.get();

            /*
             * Get only this student's enrolments.
             */
            List<Enrolment> enrolments =
                    enrolmentService.findByStudent(
                            student.getId()
                    );

            request.setAttribute(
                    "enrolments",
                    enrolments
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/enrolments.jsp"
            ).forward(request, response);

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
                    "Error loading student enrolments",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load your enrolments."
            );
        }
    }
}