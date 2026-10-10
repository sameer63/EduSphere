<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Quiz" %>

<%
    Quiz quiz =
            (Quiz) request.getAttribute("quiz");

    Course course =
            (Course) request.getAttribute("course");
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>
        <%= quiz.getTitle() %>
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        .container {
            max-width: 800px;
            margin: auto;
        }

        .card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 30px;
        }

        .title {
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 20px;
        }

        .description {
            line-height: 1.7;
            margin-bottom: 25px;
        }

        .info {
            margin: 12px 0;
        }

        .actions {
            margin-top: 30px;
        }

        a {
            margin-right: 15px;
        }

        button {
            padding: 10px 18px;
            cursor: pointer;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="card">

        <div class="title">

            <%= quiz.getTitle() %>

        </div>


        <div class="info">

            <strong>Course:</strong>

            <%= course.getTitle() %>

        </div>


        <div class="info">

            <strong>Maximum Attempts:</strong>

            <%= quiz.getMaxAttempts() %>

        </div>


        <div class="description">

            <strong>Description:</strong>

            <%
                if (quiz.getDescription() != null
                        && !quiz.getDescription().isBlank()) {
            %>

                <p>
                    <%= quiz.getDescription() %>
                </p>

            <%
                } else {
            %>

                <p>
                    No description available.
                </p>

            <%
                }
            %>

        </div>


        <div class="actions">

            <a href="<%= request.getContextPath() %>/student/courses/quizzes?courseId=<%= course.getId() %>">
                ← Back to Quizzes
            </a>

            <a href="<%= request.getContextPath() %>/student/dashboard">
                Dashboard
            </a>

        </div>


        <div class="actions">

            <form method="get"
                  action="<%= request.getContextPath() %>/student/courses/quizzes/start">

                  <input type="hidden"
                         name="csrfToken"
                         value="<%= request.getAttribute("csrfToken") %>">

                <input
                        type="hidden"
                        name="quizId"
                        value="<%= quiz.getId() %>"
                >

                <button type="submit">
                    Start Quiz
                </button>
            </form>
             <div class="actions">

                            <a href="<%= request.getContextPath() %>/student/courses/quizzes/attempts?quizId=<%= quiz.getId() %>">
                                My Attempts
                            </a>

                        </div>


        </div>

    </div>

</div>

</body>

</html>