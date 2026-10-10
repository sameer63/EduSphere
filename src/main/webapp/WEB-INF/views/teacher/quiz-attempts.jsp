<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.dto.QuizAttemptSummaryDTO" %>

<%
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    Course course = (Course) request.getAttribute("course");

    List<QuizAttemptSummaryDTO> attempts =
            (List<QuizAttemptSummaryDTO>)
                    request.getAttribute("attempts");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <title>Quiz Attempts</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
        }

        h1 {
            margin-bottom: 10px;
        }

        .course-info {
            margin-bottom: 25px;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
        }

        th,
        td {
            border: 1px solid #ddd;
            padding: 10px;
            text-align: left;
        }

        th {
            background-color: #f2f2f2;
        }

        .status {
            font-weight: bold;
        }

        .submitted {
            color: green;
        }

        .in-progress {
            color: orange;
        }

        .empty {
            margin-top: 20px;
            padding: 15px;
            background-color: #f5f5f5;
        }

        .actions {
            margin-top: 20px;
        }
    </style>
</head>

<body>

<h1>Quiz Attempts</h1>

<div class="course-info">

    <p>
        <strong>Course:</strong>
        <%= course.getTitle() %>
    </p>

    <p>
        <strong>Quiz:</strong>
        <%= quiz.getTitle() %>
    </p>

</div>

<% if (attempts == null || attempts.isEmpty()) { %>

    <div class="empty">
        No students have attempted this quiz yet.
    </div>

<% } else { %>

    <table>

        <thead>
        <tr>
            <th>Attempt ID</th>
            <th>Student</th>
            <th>Email</th>
            <th>Attempt No.</th>
            <th>Score</th>
            <th>Status</th>
            <th>Started</th>
            <th>Submitted</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>

        <% for (QuizAttemptSummaryDTO attempt : attempts) { %>

            <tr>

                <td>
                    <%= attempt.getAttemptId() %>
                </td>

                <td>
                    <%= attempt.getStudentName() %>
                </td>

                <td>
                    <%= attempt.getStudentEmail() %>
                </td>

                <td>
                    <%= attempt.getAttemptNumber() %>
                </td>

                <td>
                    <%
                        if (attempt.getScore() != null) {
                    %>
                        <%= attempt.getScore() %>
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
                        if (attempt.getSubmittedAt() != null) {
                    %>

                        <span class="status submitted">
                            Submitted
                        </span>

                    <%
                        } else {
                    %>

                        <span class="status in-progress">
                            In Progress
                        </span>

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

                    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/attempts/view?id=<%= attempt.getAttemptId() %>">
                        View
                    </a>

                </td>

            </tr>

        <% } %>

        </tbody>

    </table>

<% } %>

<div class="actions">

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/view?id=<%= quiz.getId() %>">
        Back to Quiz
    </a>

</div>

</body>
</html>