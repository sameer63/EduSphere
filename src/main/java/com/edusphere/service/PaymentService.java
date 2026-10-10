package com.edusphere.service;

import com.edusphere.dao.EnrolmentDAO;
import com.edusphere.dao.PaymentDAO;
import com.edusphere.model.Course;
import com.edusphere.model.Enrolment;
import com.edusphere.model.Payment;
import com.edusphere.model.Student;
import com.edusphere.util.DBConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class PaymentService {

    private final PaymentDAO paymentDAO =
            new PaymentDAO();

    private final StudentService studentService =
            new StudentService();

    private final CourseService courseService =
            new CourseService();

    private final EnrolmentDAO enrolmentDAO =
            new EnrolmentDAO();


    public Payment completePaymentAndEnrol(
            Long paymentId
    ) throws Exception {

        if (paymentId == null) {
            throw new IllegalArgumentException(
                    "Payment ID is required."
            );
        }

        try (
                Connection connection =
                        DBConnection.getConnection()
        ) {

            try {

                /*
                 * Start transaction.
                 */
                connection.setAutoCommit(false);

                /*
                 * Find payment using the normal DAO.
                 */
                Payment payment =
                        paymentDAO.findById(paymentId);

                if (payment == null) {
                    throw new IllegalArgumentException(
                            "Payment not found."
                    );
                }

                /*
                 * Payment must currently be PENDING.
                 */
                if (!"PENDING".equals(
                        payment.getStatus()
                )) {

                    throw new IllegalArgumentException(
                            "Only pending payments can be completed."
                    );
                }

                /*
                 * Validate student.
                 */
                Student student =
                        studentService.findById(
                                payment.getStudentId()
                        ).orElseThrow(()->new IllegalArgumentException("Student not found"));

                if (student == null) {
                    throw new IllegalArgumentException(
                            "Student not found."
                    );
                }

                /*
                 * Validate course.
                 */
                Course course =
                        courseService.findById(
                                payment.getCourseId()
                        );

                if (course == null) {
                    throw new IllegalArgumentException(
                            "Course not found."
                    );
                }

                /*
                 * Payment amount must match
                 * the current course price.
                 */
                if (payment.getAmount() == null
                        || course.getPrice() == null
                        || payment.getAmount()
                        .compareTo(course.getPrice()) != 0) {

                    throw new IllegalArgumentException(
                            "Payment amount does not match the course price."
                    );
                }

                /*
                 * Check whether enrolment already exists.
                 */
                Optional<Enrolment> optionalExistingEnrolment =
                        enrolmentDAO.findByStudentAndCourse(
                                payment.getStudentId(),
                                payment.getCourseId()
                        );

                Enrolment existingEnrolment;
                if(optionalExistingEnrolment.isPresent()){
                     existingEnrolment=optionalExistingEnrolment.get();

                        if ("ACTIVE".equals(
                                existingEnrolment.getStatus()
                        )
                                || "COMPLETED".equals(
                                existingEnrolment.getStatus()
                        )) {

                            throw new IllegalArgumentException(
                                    "Student is already enrolled in this course."
                            );
                        }

                }



                /*
                 * Mark payment SUCCESS.
                 */
                Payment successfulPayment =
                        paymentDAO.updateStatus(
                                connection,
                                paymentId,
                                "SUCCESS"
                        );

                if (successfulPayment == null) {

                    throw new IllegalArgumentException(
                            "Unable to complete payment."
                    );
                }

                /*
                 * Create ACTIVE enrolment.
                 */
                Enrolment enrolment =
                        new Enrolment();

                enrolment.setStudentId(
                        payment.getStudentId()
                );

                enrolment.setCourseId(
                        payment.getCourseId()
                );

                enrolment.setStatus(
                        "ACTIVE"
                );

                enrolmentDAO.createEnrolment(
                        connection,
                        enrolment
                );

                /*
                 * Everything succeeded.
                 */
                connection.commit();

                return successfulPayment;

            } catch (Exception e) {

                /*
                 * Payment or enrolment failed.
                 * Undo everything.
                 */
                connection.rollback();

                throw e;
            }
        }
    }


    /*
     * Create Payment
     */
    public Payment createPayment(
            Long studentId,
            Long courseId
    ) throws Exception {

        validateIds(
                studentId,
                courseId
        );

        /*
         * Validate student
         */
        Student student =
                studentService.findById(studentId)
                        .orElseThrow(()-> new IllegalArgumentException("Student not found"));

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student not found."
            );
        }

        /*
         * Validate course
         */
        Course course =
                courseService.findById(courseId);

        if (course == null) {

            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        /*
         * Only published courses can be purchased.
         */
        if (!"PUBLISHED".equals(
                course.getStatus()
        )) {

            throw new IllegalArgumentException(
                    "Only published courses can be purchased."
            );
        }

        /*
         * Course price must exist.
         */
        if (course.getPrice() == null) {

            throw new IllegalArgumentException(
                    "Course price is not configured."
            );
        }

        /*
         * Price must not be negative.
         */
        if (course.getPrice()
                .compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "Course price cannot be negative."
            );
        }

        /*
         * A free course does not require payment.
         */
        if (course.getPrice()
                .compareTo(BigDecimal.ZERO) == 0) {

            throw new IllegalArgumentException(
                    "This course is free and does not require payment."
            );
        }

        /*
         * Prevent duplicate successful payment.
         */
        List<Payment> existingPayments =
                paymentDAO.findByStudent(studentId);

        for (Payment existingPayment
                : existingPayments) {

            if (!courseId.equals(
                    existingPayment.getCourseId()
            )) {
                continue;
            }

            if ("SUCCESS".equals(
                    existingPayment.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "You have already successfully paid for this course."
                );
            }

            /*
             * Don't create another PENDING payment
             * for the same course.
             */
            if ("PENDING".equals(
                    existingPayment.getStatus()
            )) {

                throw new IllegalArgumentException(
                        "A payment is already pending for this course."
                );
            }
        }

        /*
         * Generate a temporary transaction reference.
         *
         * Later this will come from the actual
         * payment gateway.
         */
        String transactionReference =
                "DEMO-"
                        + UUID.randomUUID();

        Payment payment =
                new Payment(
                        studentId,
                        courseId,
                        course.getPrice(),
                        "INR",
                        "PENDING",
                        transactionReference
                );

        return paymentDAO.createPayment(
                payment
        );
    }

    public Payment refundPayment(Long paymentId) throws Exception {

        if (paymentId == null) {
            throw new IllegalArgumentException("Payment ID is required.");
        }

        Connection connection = null;

        try {
            connection = DBConnection.getConnection();
            connection.setAutoCommit(false);

            // 1. Find payment
            Payment payment = paymentDAO.findById(paymentId);

            if (payment == null) {
                throw new IllegalArgumentException(
                        "Payment not found."
                );
            }

            // 2. Payment must be SUCCESS
            if (!"SUCCESS".equals(payment.getStatus())) {
                throw new IllegalArgumentException(
                        "Only a successful payment can be refunded."
                );
            }

            // 3. Validate student
            Student student =
                    studentService.findById(payment.getStudentId())
                            .orElseThrow(()->new IllegalArgumentException("Student not found"));

            if (student == null) {
                throw new IllegalArgumentException(
                        "Student not found."
                );
            }

            // 4. Validate course
            Course course =
                    courseService.findById(payment.getCourseId());

            if (course == null) {
                throw new IllegalArgumentException(
                        "Course not found."
                );
            }

            // 5. Refund payment
            Payment refundedPayment =
                    paymentDAO.updateStatus(
                            connection,
                            paymentId,
                            "REFUNDED"
                    );

            if (refundedPayment == null) {
                throw new IllegalStateException(
                        "Payment could not be refunded."
                );
            }

            // 6. Cancel ACTIVE enrolment
            Enrolment cancelledEnrolment =
                    enrolmentDAO.cancelEnrolment(
                            connection,
                            payment.getStudentId(),
                            payment.getCourseId()
                    );

            if (cancelledEnrolment == null) {
                throw new IllegalStateException(
                        "Active enrolment not found."
                );
            }

            // 7. Commit both operations
            connection.commit();

            return refundedPayment;

        } catch (Exception exception) {

            if (connection != null) {
                connection.rollback();
            }

            throw exception;

        } finally {

            if (connection != null) {
                connection.setAutoCommit(true);
                connection.close();
            }
        }
    }

    /*
     * Find Payment By ID
     */
    public Payment findById(
            Long paymentId
    ) throws Exception {

        if (paymentId == null) {

            throw new IllegalArgumentException(
                    "Payment ID is required."
            );
        }

        return paymentDAO.findById(
                paymentId
        );
    }


    /*
     * Find Student Payments
     */
    public List<Payment> findByStudent(
            Long studentId
    ) throws Exception {

        if (studentId == null) {

            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        Student student =
                studentService.findById(studentId)
                        .orElseThrow(()-> new IllegalArgumentException("Student not found"));

        if (student == null) {

            throw new IllegalArgumentException(
                    "Student not found."
            );
        }

        return paymentDAO.findByStudent(
                studentId
        );
    }


    /*
     * Find Course Payments
     */
    public List<Payment> findByCourse(
            Long courseId
    ) throws Exception {

        if (courseId == null) {

            throw new IllegalArgumentException(
                    "Course ID is required."
            );
        }

        Course course =
                courseService.findById(courseId);

        if (course == null) {

            throw new IllegalArgumentException(
                    "Course not found."
            );
        }

        return paymentDAO.findByCourse(
                courseId
        );
    }


    /*
     * Update Payment Status
     */
    public Payment updateStatus(
            Long paymentId,
            String newStatus
    ) throws Exception {

        if (paymentId == null) {

            throw new IllegalArgumentException(
                    "Payment ID is required."
            );
        }

        if (newStatus == null ||
                newStatus.isBlank()) {

            throw new IllegalArgumentException(
                    "Payment status is required."
            );
        }

        newStatus =
                newStatus.trim().toUpperCase();

        validateStatus(
                newStatus
        );

        Payment payment =
                paymentDAO.findById(
                        paymentId
                );

        if (payment == null) {

            throw new IllegalArgumentException(
                    "Payment not found."
            );
        }

        String currentStatus =
                payment.getStatus();

        validateStatusTransition(
                currentStatus,
                newStatus
        );

        return paymentDAO.updateStatus(
                paymentId,
                newStatus
        );
    }


    /*
     * Check whether a student has
     * successfully paid for a course.
     */
    public boolean hasSuccessfulPayment(
            Long studentId,
            Long courseId
    ) throws Exception {

        if (studentId == null ||
                courseId == null) {

            return false;
        }

        List<Payment> payments =
                paymentDAO.findByStudent(
                        studentId
                );

        for (Payment payment : payments) {

            if (courseId.equals(
                    payment.getCourseId()
            )
                    && "SUCCESS".equals(
                    payment.getStatus()
            )) {

                return true;
            }
        }

        return false;
    }


    /*
     * Validate IDs
     */
    private void validateIds(
            Long studentId,
            Long courseId
    ) {

        if (studentId == null) {

            throw new IllegalArgumentException(
                    "Student ID is required."
            );
        }

        if (courseId == null) {

            throw new IllegalArgumentException(
                    "Course ID is required."
            );
        }
    }


    /*
     * Validate Payment Status
     */
    private void validateStatus(
            String status
    ) {

        if (!"PENDING".equals(status)
                && !"SUCCESS".equals(status)
                && !"FAILED".equals(status)
                && !"REFUNDED".equals(status)) {

            throw new IllegalArgumentException(
                    "Invalid payment status."
            );
        }
    }


    /*
     * Validate allowed status transitions.
     *
     * PENDING  → SUCCESS
     * PENDING  → FAILED
     * SUCCESS  → REFUNDED
     *
     * Everything else is rejected.
     */
    private void validateStatusTransition(
            String currentStatus,
            String newStatus
    ) {

        if (currentStatus == null) {

            throw new IllegalArgumentException(
                    "Current payment status is invalid."
            );
        }

        if (currentStatus.equals(
                newStatus
        )) {

            throw new IllegalArgumentException(
                    "Payment already has this status."
            );
        }

        if ("PENDING".equals(currentStatus)) {

            if ("SUCCESS".equals(newStatus)
                    || "FAILED".equals(newStatus)) {

                return;
            }
        }

        if ("SUCCESS".equals(currentStatus)
                && "REFUNDED".equals(newStatus)) {

            return;
        }

        throw new IllegalArgumentException(
                "Invalid payment status transition: "
                        + currentStatus
                        + " → "
                        + newStatus
        );
    }
}