<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Payment" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    Payment payment =
            (Payment) request.getAttribute("payment");

    Course course =
            (Course) request.getAttribute("course");

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd-MM-yyyy HH:mm:ss"
            );
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>Payment</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background: #f7f7f7;
        }

        .container {
            max-width: 700px;
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

        .row {
            padding: 12px 0;
            border-bottom: 1px solid #eee;
        }

        .label {
            font-weight: bold;
        }

        .amount {
            font-size: 28px;
            font-weight: bold;
            margin: 20px 0;
        }

        .status {
            display: inline-block;
            padding: 8px 14px;
            border-radius: 5px;
            font-weight: bold;
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

        .actions {
            margin-top: 25px;
        }

        .actions a {
            margin-right: 15px;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="card">

        <h1>Payment Details</h1>

        <div class="row">

            <span class="label">
                Course:
            </span>

            <%= course.getTitle() %>

        </div>


        <div class="row">

            <span class="label">
                Payment ID:
            </span>

            <%= payment.getId() %>

        </div>


        <div class="amount">

            <%= payment.getCurrency() %>
            <%= payment.getAmount() %>

        </div>


        <div class="row">

            <span class="label">
                Status:
            </span>

            <br><br>

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

        </div>
        <%
            if ("PENDING".equals(payment.getStatus())) {
        %>

            <div class="actions">

                <form method="post"
                      action="<%= request.getContextPath() %>/student/payments/complete-demo">

                      <input type="hidden"
                             name="csrfToken"
                             value="<%= request.getAttribute("csrfToken") %>">

                    <input type="hidden"
                           name="paymentId"
                           value="<%= payment.getId() %>">

                    <button type="submit">
                        Complete Demo Payment
                    </button>

                </form>

                <p>
                    <small>
                        Development/testing only.
                        This will be replaced by a real payment gateway.
                    </small>
                </p>

            </div>

        <%
            }
        %>

        <div class="row">

            <span class="label">
                Transaction Reference:
            </span>

            <%= payment.getTransactionReference() %>

        </div>


        <div class="row">

            <span class="label">
                Created:
            </span>

            <%= payment.getCreatedAt() != null
                    ? payment.getCreatedAt()
                        .format(formatter)
                    : "-" %>

        </div>
        <% if ("SUCCESS".equals(payment.getStatus())) { %>

            <div class="actions">

                <form method="post"
                      action="<%= request.getContextPath() %>/student/payments/refund"
                      onsubmit="return confirm(
                          'Are you sure you want to refund this payment?'
                      );">

                      <input type="hidden"
                             name="csrfToken"
                             value="<%= request.getAttribute("csrfToken") %>">

                    <input type="hidden"
                           name="paymentId"
                           value="<%= payment.getId() %>">

                    <button type="submit">
                        Refund Payment
                    </button>

                </form>

            </div>

        <% } %>


        <div class="actions">

            <a href="<%= request.getContextPath() %>/student/enrolments">
                My Enrolments
            </a>

            |

            <a href="<%= request.getContextPath() %>/student/courses">
                Courses
            </a>

        </div>

    </div>

</div>

</body>

</html>