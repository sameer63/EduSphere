<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.QuizAttempt" %>
<%@ page import="com.edusphere.model.Student" %>

<%
    Student student =
            (Student) request.getAttribute("student");

    QuizAttempt attempt =
            (QuizAttempt) request.getAttribute("attempt");

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
        Quiz Result - <%= quiz.getTitle() %>
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: #f5f5f5;
        }

        .container {
            max-width: 700px;
            margin: auto;
        }

        .card {
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 30px;
        }

        .title {
            font-size: 30px;
            font-weight: bold;
            margin-bottom: 25px;
        }

        .info {
            margin: 15px 0;
        }

        .score {
            font-size: 36px;
            font-weight: bold;
            margin: 30px 0;
        }

        .percentage {
            font-size: 24px;
            margin-bottom: 30px;
        }

        .actions {
            margin-top: 30px;
        }

        a {
            margin-right: 15px;
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

            <strong>Attempt:</strong>

            #<%= attempt.getAttemptNumber() %>

        </div>

        <div class="score">

            Score:

            <%= request.getAttribute("score") %>
            /
            <%= request.getAttribute("totalMarks") %>

        </div>

        <div class="percentage">

            Percentage:

            <%= request.getAttribute("percentage") %>%

        </div>

        <div class="info">

            <strong>Started:</strong>

            <%= attempt.getStartedAt() %>

        </div>

        <div class="info">

            <strong>Submitted:</strong>

            <%= attempt.getSubmittedAt() %>

        </div>

        <div class="actions">

            <a href="<%= request.getContextPath() %>/student/courses/quizzes?courseId=<%= course.getId() %>">
                ← Back to Quizzes
            </a>

            <a href="<%= request.getContextPath() %>/student/dashboard">
                Dashboard
            </a>

        </div>

    </div>

</div>

</body>

</html>