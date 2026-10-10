package com.edusphere.controller;

import com.edusphere.model.Payment;
import com.edusphere.model.Student;
import com.edusphere.service.PaymentService;

import com.edusphere.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/student/payments/complete-demo")
public class CompleteDemoPaymentServlet
        extends HttpServlet {

    private final PaymentService paymentService =
            new PaymentService();
    private final StudentService studentService=new StudentService();

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

            String paymentIdParameter =
                    request.getParameter("paymentId");

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

            /*
             * In a real application, the payment gateway
             * would call the verification flow instead.
             */
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
             * Verify that the payment belongs
             * to the logged-in student.
             */
            Student student =studentService.findByUserId(userId)
                    .orElseThrow(()->new IllegalArgumentException("Student not found"));

            if (student == null) {

                throw new IllegalArgumentException(
                        "Student profile not found."
                );
            }

            if (!student.getId().equals(
                    payment.getStudentId()
            )) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to complete this payment."
                );

                return;
            }

            paymentService.completePaymentAndEnrol(
                    paymentId
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/student/payments/view?id="
                            + paymentId
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
                    "Unable to complete payment.",
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