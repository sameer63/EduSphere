<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Payment" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    List<Payment> payments =
            (List<Payment>)
                    request.getAttribute("payments");

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd-MM-yyyy HH:mm:ss"
            );
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>My Payments</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background: #f7f7f7;
        }

        .container {
            max-width: 1200px;
            margin: auto;
        }

        .card {
            background: white;
            padding: 25px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        h1 {
            margin-top: 0;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th,
        td {
            border: 1px solid #ddd;
            padding: 12px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }

        .status {
            display: inline-block;
            padding: 6px 10px;
            border-radius: 5px;
            font-weight: bold;
            font-size: 13px;
        }

        .pending {
            background: #fff3cd;
            color: #856404;
        }

        .success {
            background: #d4edda;
            color: #155724;
        }

        .failed {
            background: #f8d7da;
            color: #721c24;
        }

        .refunded {
            background: #e2e3e5;
            color: #383d41;
        }

        .empty {
            margin-top: 20px;
            padding: 15px;
            background: #f5f5f5;
        }

        .actions {
            margin-top: 25px;
        }

        .actions a {
            margin-right: 15px;
        }

        @media (max-width: 900px) {

            table {
                font-size: 14px;
            }

            th,
            td {
                padding: 8px;
            }
        }

    </style>

</head>

<body>

<div class="container">

    <div class="card">

        <h1>My Payments</h1>

        <%
            if (payments == null ||
                    payments.isEmpty()) {
        %>

            <div class="empty">

                You have no payment records yet.

            </div>

        <%
            } else {
        %>

            <table>

                <thead>

                <tr>

                    <th>
                        Payment ID
                    </th>

                    <th>
                        Course ID
                    </th>

                    <th>
                        Amount
                    </th>

                    <th>
                        Status
                    </th>

                    <th>
                        Transaction Reference
                    </th>

                    <th>
                        Created
                    </th>

                    <th>
                        Action
                    </th>

                </tr>

                </thead>


                <tbody>

                <%
                    for (Payment payment : payments) {
                %>

                    <tr>

                        <td>
                            <%= payment.getId() %>
                        </td>

                        <td>
                            <%= payment.getCourseId() %>
                        </td>

                        <td>

                            <%= payment.getCurrency() %>
                            <%= payment.getAmount() %>

                        </td>

                        <td>

                            <%
                                String status =
                                        payment.getStatus();

                                String statusClass =
                                        status == null
                                                ? ""
                                                : status.toLowerCase();
                            %>

                            <span class="status <%= statusClass %>">

                                <%= status %>

                            </span>

                        </td>

                        <td>
                            <%= payment.getTransactionReference() %>
                        </td>

                        <td>

                            <%= payment.getCreatedAt() != null
                                    ? payment.getCreatedAt()
                                        .format(formatter)
                                    : "-" %>

                        </td>

                        <td>

                            <a href="<%= request.getContextPath() %>/student/payments/view?id=<%= payment.getId() %>">
                                View
                            </a>

                        </td>

                    </tr>

                <%
                    }
                %>

                </tbody>

            </table>

        <%
            }
        %>


        <div class="actions">

            <a href="<%= request.getContextPath() %>/student/courses">
                Browse Courses
            </a>

            |

            <a href="<%= request.getContextPath() %>/student/enrolments">
                My Enrolments
            </a>

        </div>

    </div>

</div>

</body>

</html>