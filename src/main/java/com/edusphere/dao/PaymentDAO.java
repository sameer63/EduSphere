package com.edusphere.dao;

import com.edusphere.util.DBConnection;
import com.edusphere.model.Payment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    /*
     * Create Payment
     */
    public Payment createPayment(
            Payment payment
    ) throws Exception {

        String sql = """
                INSERT INTO payments (
                    student_id,
                    course_id,
                    amount,
                    currency,
                    status,
                    transaction_reference
                )
                VALUES (?, ?, ?, ?, ?, ?)
                RETURNING
                    id,
                    student_id,
                    course_id,
                    amount,
                    currency,
                    status,
                    transaction_reference,
                    created_at
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    payment.getStudentId()
            );

            statement.setLong(
                    2,
                    payment.getCourseId()
            );

            statement.setBigDecimal(
                    3,
                    payment.getAmount()
            );

            statement.setString(
                    4,
                    payment.getCurrency()
            );

            statement.setString(
                    5,
                    payment.getStatus()
            );

            statement.setString(
                    6,
                    payment.getTransactionReference()
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return mapPayment(resultSet);
                }
            }
        }

        return null;
    }


    /*
     * Find Payment By ID
     */
    public Payment findById(
            Long paymentId
    ) throws Exception {

        String sql = """
                SELECT
                    id,
                    student_id,
                    course_id,
                    amount,
                    currency,
                    status,
                    transaction_reference,
                    created_at
                FROM payments
                WHERE id = ?
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    paymentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return mapPayment(resultSet);
                }
            }
        }

        return null;
    }


    /*
     * Find Payments By Student
     */
    public List<Payment> findByStudent(
            Long studentId
    ) throws Exception {

        String sql = """
                SELECT
                    id,
                    student_id,
                    course_id,
                    amount,
                    currency,
                    status,
                    transaction_reference,
                    created_at
                FROM payments
                WHERE student_id = ?
                ORDER BY created_at DESC
                """;

        List<Payment> payments =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    studentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    payments.add(
                            mapPayment(resultSet)
                    );
                }
            }
        }

        return payments;
    }


    /*
     * Find Payments By Course
     */
    public List<Payment> findByCourse(
            Long courseId
    ) throws Exception {

        String sql = """
                SELECT
                    id,
                    student_id,
                    course_id,
                    amount,
                    currency,
                    status,
                    transaction_reference,
                    created_at
                FROM payments
                WHERE course_id = ?
                ORDER BY created_at DESC
                """;

        List<Payment> payments =
                new ArrayList<>();

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setLong(
                    1,
                    courseId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    payments.add(
                            mapPayment(resultSet)
                    );
                }
            }
        }

        return payments;
    }


    public Payment updateStatus(
            Connection connection,
            Long paymentId,
            String status
    ) throws Exception {

        String sql = """
            UPDATE payments
            SET status = ?
            WHERE id = ?
            RETURNING
                id,
                student_id,
                course_id,
                amount,
                currency,
                status,
                transaction_reference,
                created_at
            """;

        try (
                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(1, status);
            statement.setLong(2, paymentId);

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return mapPayment(resultSet);
                }
            }
        }

        return null;
    }
    /*
     * Update Payment Status
     */
    public Payment updateStatus(
            Long paymentId,
            String status
    ) throws Exception {

        String sql = """
                UPDATE payments
                SET status = ?
                WHERE id = ?
                RETURNING
                    id,
                    student_id,
                    course_id,
                    amount,
                    currency,
                    status,
                    transaction_reference,
                    created_at
                """;

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    status
            );

            statement.setLong(
                    2,
                    paymentId
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                if (resultSet.next()) {
                    return mapPayment(resultSet);
                }
            }
        }

        return null;
    }


    /*
     * Map ResultSet → Payment
     */
    private Payment mapPayment(
            ResultSet resultSet
    ) throws Exception {

        Payment payment =
                new Payment();

        payment.setId(
                resultSet.getLong("id")
        );

        payment.setStudentId(
                resultSet.getLong("student_id")
        );

        payment.setCourseId(
                resultSet.getLong("course_id")
        );

        payment.setAmount(
                resultSet.getBigDecimal("amount")
        );

        payment.setCurrency(
                resultSet.getString("currency")
        );

        payment.setStatus(
                resultSet.getString("status")
        );

        payment.setTransactionReference(
                resultSet.getString(
                        "transaction_reference"
                )
        );

        if (resultSet.getTimestamp("created_at")
                != null) {

            payment.setCreatedAt(
                    resultSet.getTimestamp(
                            "created_at"
                    ).toLocalDateTime()
            );
        }

        return payment;
    }
}