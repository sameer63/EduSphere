<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Quiz" %>

<%
    Course course =
            (Course) request.getAttribute("course");

    List<Quiz> quizzes =
            (List<Quiz>) request.getAttribute("quizzes");
%>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <title>
        Quizzes - <%= course.getTitle() %>
    </title>
</head>

<body>

<h1>
    Quizzes
</h1>

<h2>
    Course: <%= course.getTitle() %>
</h2>

<hr>

<% if (quizzes == null || quizzes.isEmpty()) { %>

    <p>
        No quizzes are available for this course yet.
    </p>

<% } else { %>

    <table border="1" cellpadding="8" cellspacing="0">

        <thead>
        <tr>
            <th>ID</th>
            <th>Title</th>
            <th>Maximum Attempts</th>
            <th>Created At</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>

        <% for (Quiz quiz : quizzes) { %>

            <tr>

                <td>
                    <%= quiz.getId() %>
                </td>

                <td>
                    <%= quiz.getTitle() %>
                </td>

                <td>
                    <%= quiz.getMaxAttempts() %>
                </td>

                <td>
                    <%= quiz.getCreatedAt() %>
                </td>

                <td>

                    <a href="<%= request.getContextPath() %>/student/courses/quizzes/view?id=<%= quiz.getId() %>">
                        View Quiz
                    </a>

                </td>

            </tr>

        <% } %>

        </tbody>

    </table>

<% } %>

<hr>

<a href="<%= request.getContextPath() %>/student/courses/view?id=<%= course.getId() %>">
    Back to Course
</a>

</body>
</html>