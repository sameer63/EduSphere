<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>

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
        Manage Quizzes - EduSphere
    </title>

</head>

<body>

<h1>Manage Quizzes</h1>

<p>
    <strong>Course:</strong>
    <%= course.getTitle() %>
</p>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/create?courseId=<%= course.getId() %>">

        Create New Quiz

    </a>

</p>

<hr>

<%
    if (quizzes == null || quizzes.isEmpty()) {
%>

    <h3>No quizzes yet.</h3>

    <p>
        Create your first quiz for this course.
    </p>

<%
    } else {
%>

    <table border="1"
           cellpadding="8"
           cellspacing="0">

        <thead>

        <tr>

            <th>
                ID
            </th>

            <th>
                Title
            </th>

            <th>
                Maximum Attempts
            </th>

            <th>
                Created At
            </th>

            <th>
                Action
            </th>

        </tr>

        </thead>

        <tbody>

        <%
            for (Quiz quiz : quizzes) {
        %>

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

                    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/view?id=<%= quiz.getId() %>">

                        View Quiz

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

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/view?id=<%= course.getId() %>">

        Back to Course

    </a>

</p>

</body>

</html>