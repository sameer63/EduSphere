package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Payment;
import com.edusphere.model.Student;
import com.edusphere.service.CourseService;
import com.edusphere.service.PaymentService;
import com.edusphere.service.StudentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/student/payments/create")
public class CreatePaymentServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final CourseService courseService =
            new CourseService();

    private final PaymentService paymentService =
            new PaymentService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

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
             * Get logged-in student's profile.
             */
            Student student =
                    studentService.findByUserId(userId)
                            .orElseThrow(()->new IllegalArgumentException("Student not found"));

            if (student == null) {

                throw new IllegalArgumentException(
                        "Student profile not found."
                );
            }

            /*
             * Only courseId comes from the browser.
             */
            String courseIdParameter =
                    request.getParameter("courseId");

            if (courseIdParameter == null ||
                    courseIdParameter.isBlank()) {

                throw new IllegalArgumentException(
                        "Course ID is required."
                );
            }

            Long courseId;

            try {

                courseId =
                        Long.parseLong(courseIdParameter);

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "Invalid course ID."
                );
            }

            /*
             * Verify that the course exists before
             * attempting payment.
             */
            Course course =
                    courseService.findById(courseId);

            if (course == null) {

                throw new IllegalArgumentException(
                        "Course not found."
                );
            }

            /*
             * Create payment.
             *
             * PaymentService obtains the actual
             * course price from the database.
             */
            Payment payment =
                    paymentService.createPayment(
                            student.getId(),
                            courseId
                    );

            /*
             * Redirect to payment details page.
             */
            response.sendRedirect(
                    request.getContextPath()
                            + "/student/payments/view?id="
                            + payment.getId()
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

            throw new ServletException(
                    "Unable to create payment.",
                    e
            );
        }
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        response.sendError(
                HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                "GET is not allowed."
        );
    }
}