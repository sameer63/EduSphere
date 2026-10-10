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

@WebServlet("/student/payments/refund")
public class RefundPaymentServlet extends HttpServlet {

    private final PaymentService paymentService =
            new PaymentService();

    private final StudentService studentService =
            new StudentService();

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws ServletException, IOException {

        HttpSession session = request.getSession(false);

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

            Student student =
                    studentService.findByUserId(userId)
                            .orElseThrow(()->new IllegalArgumentException("Student not found"));

            if (student == null) {
                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );
                return;
            }

            String paymentIdParameter =
                    request.getParameter("paymentId");

            if (paymentIdParameter == null ||
                    paymentIdParameter.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Payment ID is required."
                );
                return;
            }

            Long paymentId;

            try {
                paymentId =
                        Long.parseLong(paymentIdParameter);

            } catch (NumberFormatException exception) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid payment ID."
                );
                return;
            }

            // Load payment first
            Payment payment =
                    paymentService.findById(paymentId);

            if (payment == null) {
                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Payment not found."
                );
                return;
            }

            // Security check:
            // Student can refund only their own payment.
            if (!student.getId().equals(payment.getStudentId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to refund this payment."
                );
                return;
            }

            // Perform refund
            paymentService.refundPayment(paymentId);

            // PRG pattern
            response.sendRedirect(
                    request.getContextPath()
                            + "/student/payments/view?id="
                            + paymentId
            );

        } catch (IllegalArgumentException exception) {

            request.setAttribute(
                    "error",
                    exception.getMessage()
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/error/error.jsp"
            ).forward(request, response);

        } catch (Exception exception) {

            throw new ServletException(
                    "Unable to refund payment.",
                    exception
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