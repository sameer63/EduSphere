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
import java.util.List;

@WebServlet("/student/payments")
public class StudentPaymentsServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final PaymentService paymentService =
            new PaymentService();

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

            List<Payment> payments =
                    paymentService.findByStudent(
                            student.getId()
                    );

            request.setAttribute(
                    "student",
                    student
            );

            request.setAttribute(
                    "payments",
                    payments
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/payments.jsp"
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
                    "Unable to load payment history.",
                    e
            );
        }
    }
}