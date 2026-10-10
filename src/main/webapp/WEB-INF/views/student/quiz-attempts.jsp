<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.QuizAttempt" %>
<%@ page import="com.edusphere.model.QuizQuestion" %>

<%
    Quiz quiz =
            (Quiz) request.getAttribute("quiz");

    Course course =
            (Course) request.getAttribute("course");

    List<QuizAttempt> attempts =
            (List<QuizAttempt>)
                    request.getAttribute("attempts");
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>
        Quiz Attempts - <%= quiz.getTitle() %>
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        .container {
            max-width: 900px;
            margin: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 25px;
        }

        th,
        td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }

        th {
            background: #f5f5f5;
        }

        .actions {
            margin-top: 25px;
        }

        a {
            margin-right: 15px;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>
        <%= quiz.getTitle() %>
    </h1>

    <p>
        <strong>Course:</strong>
        <%= course.getTitle() %>
    </p>

    <p>
        <strong>Maximum Attempts:</strong>
        <%= quiz.getMaxAttempts() %>
    </p>

    <hr>

    <h2>
        My Attempts
    </h2>

    <%
        if (attempts == null || attempts.isEmpty()) {
    %>

        <p>
            You have not attempted this quiz yet.
        </p>

    <%
        } else {
    %>

        <table>

            <thead>

            <tr>
                <th>Attempt</th>
                <th>Score</th>
                <th>Status</th>
                <th>Started</th>
                <th>Submitted</th>
                <th>Action</th>
            </tr>

            </thead>

            <tbody>

            <%
                for (QuizAttempt attempt : attempts) {

                    boolean submitted =
                            attempt.getSubmittedAt() != null;
            %>

                <tr>

                    <td>
                        #<%= attempt.getAttemptNumber() %>
                    </td>

                    <td>

                        <%
                            if (submitted) {
                        %>

                            <%= attempt.getScore() %>

                        <%
                            } else {
                        %>

                            Not submitted

                        <%
                            }
                        %>

                    </td>

                    <td>

                        <%
                            if (submitted) {
                        %>

                            Submitted

                        <%
                            } else {
                        %>

                            In Progress

                        <%
                            }
                        %>

                    </td>

                    <td>
                        <%= attempt.getStartedAt() %>
                    </td>

                    <td>

                        <%
                            if (attempt.getSubmittedAt() != null) {
                        %>

                            <%= attempt.getSubmittedAt() %>

                        <%
                            } else {
                        %>

                            -

                        <%
                            }
                        %>

                    </td>

                    <td>

                        <%
                            if (submitted) {
                        %>

                            <a href="<%= request.getContextPath() %>/student/courses/quizzes/result?id=<%= attempt.getId() %>">
                                View Result
                            </a>

                        <%
                            } else {
                        %>

                            <a href="<%= request.getContextPath() %>/student/courses/quizzes/attempt?id=<%= attempt.getId() %>">
                                Resume
                            </a>

                        <%
                            }
                        %>

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

        <a href="<%= request.getContextPath() %>/student/courses/quizzes/view?id=<%= quiz.getId() %>">
            ← Back to Quiz
        </a>

        <a href="<%= request.getContextPath() %>/student/dashboard">
            Dashboard
        </a>

    </div>

</div>

</body>

</html>