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

@WebServlet("/student/payments/view")
public class StudentPaymentViewServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final PaymentService paymentService =
            new PaymentService();

    private final CourseService courseService =
            new CourseService();

    @Override
    protected void doGet(
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

            Student student =
                    studentService.findByUserId(userId)
                            .orElseThrow(()->new IllegalArgumentException("Student not found"));

            if (student == null) {

                throw new IllegalArgumentException(
                        "Student profile not found."
                );
            }

            String paymentIdParameter =
                    request.getParameter("id");

            if (paymentIdParameter == null ||
                    paymentIdParameter.isBlank()) {

                throw new IllegalArgumentException(
                        "Payment ID is required."
                );
            }

            Long paymentId;

            try {

                paymentId =
                        Long.parseLong(paymentIdParameter);

            } catch (NumberFormatException e) {

                throw new IllegalArgumentException(
                        "Invalid payment ID."
                );
            }

            Payment payment =
                    paymentService.findById(
                            paymentId
                    );

            if (payment == null) {

                throw new IllegalArgumentException(
                        "Payment not found."
                );
            }

            /*
             * Ownership check.
             *
             * A student must not be able to view
             * another student's payment by changing
             * ?id=...
             */
            if (!student.getId().equals(
                    payment.getStudentId()
            )) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to view this payment."
                );

                return;
            }

            Course course =
                    courseService.findById(
                            payment.getCourseId()
                    );

            if (course == null) {

                throw new IllegalArgumentException(
                        "Course not found."
                );
            }

            request.setAttribute(
                    "student",
                    student
            );

            request.setAttribute(
                    "payment",
                    payment
            );

            request.setAttribute(
                    "course",
                    course
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/payment-view.jsp"
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

            throw new ServletException(
                    "Unable to load payment.",
                    e
            );
        }
    }
}