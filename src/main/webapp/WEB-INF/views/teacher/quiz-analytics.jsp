<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.dto.QuizAnalyticsDTO" %>
<%@ page import="java.math.BigDecimal" %>

<%
    Quiz quiz =
            (Quiz) request.getAttribute("quiz");

    Course course =
            (Course) request.getAttribute("course");

    QuizAnalyticsDTO analytics =
            (QuizAnalyticsDTO)
                    request.getAttribute("analytics");

    BigDecimal totalMarks =
            (BigDecimal)
                    request.getAttribute("totalMarks");
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>Quiz Analytics</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 30px;
            background: #f7f7f7;
        }

        .container {
            max-width: 1100px;
            margin: auto;
        }

        .card {
            background: white;
            padding: 25px;
            margin-bottom: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
        }

        h1 {
            margin-top: 0;
        }

        .subtitle {
            color: #666;
            margin-bottom: 25px;
        }

        .analytics-grid {
            display: grid;
            grid-template-columns:
                    repeat(3, 1fr);
            gap: 20px;
        }

        .stat {
            border: 1px solid #ddd;
            padding: 20px;
            border-radius: 8px;
        }

        .stat-label {
            color: #666;
            font-size: 14px;
        }

        .stat-value {
            font-size: 28px;
            font-weight: bold;
            margin-top: 8px;
        }

        .stat-description {
            color: #777;
            margin-top: 5px;
            font-size: 13px;
        }

        .actions {
            margin-top: 20px;
        }

        .actions a {
            margin-right: 15px;
        }

        @media (max-width: 800px) {

            .analytics-grid {
                grid-template-columns:
                        repeat(2, 1fr);
            }
        }

        @media (max-width: 500px) {

            .analytics-grid {
                grid-template-columns: 1fr;
            }
        }

    </style>

</head>

<body>

<div class="container">

    <div class="card">

        <h1>Quiz Analytics</h1>

        <p class="subtitle">
            <strong>Course:</strong>
            <%= course.getTitle() %>
            <br>
            <strong>Quiz:</strong>
            <%= quiz.getTitle() %>
            <br>
            <strong>Total Marks:</strong>
            <%= totalMarks %>
        </p>

    </div>


    <div class="card">

        <h2>Attempt Statistics</h2>

        <div class="analytics-grid">


            <!-- Total Attempts -->

            <div class="stat">

                <div class="stat-label">
                    Total Attempts
                </div>

                <div class="stat-value">
                    <%= analytics.getTotalAttempts() %>
                </div>

                <div class="stat-description">
                    All attempts started by students
                </div>

            </div>


            <!-- Submitted -->

            <div class="stat">

                <div class="stat-label">
                    Submitted Attempts
                </div>

                <div class="stat-value">
                    <%= analytics.getSubmittedAttempts() %>
                </div>

                <div class="stat-description">
                    Completed attempts
                </div>

            </div>


            <!-- In Progress -->

            <div class="stat">

                <div class="stat-label">
                    In Progress
                </div>

                <div class="stat-value">
                    <%= analytics.getInProgressAttempts() %>
                </div>

                <div class="stat-description">
                    Attempts not yet submitted
                </div>

            </div>


            <!-- Average Score -->

            <div class="stat">

                <div class="stat-label">
                    Average Score
                </div>

                <div class="stat-value">

                    <%
                        if (analytics.getAverageScore() != null) {
                    %>

                        <%= analytics.getAverageScore()
                                .setScale(
                                        2,
                                        java.math.RoundingMode.HALF_UP
                                ) %>

                    <%
                        } else {
                    %>

                        -

                    <%
                        }
                    %>

                </div>

                <div class="stat-description">
                    Average marks obtained
                </div>

            </div>


            <!-- Highest Score -->

            <div class="stat">

                <div class="stat-label">
                    Highest Score
                </div>

                <div class="stat-value">

                    <%
                        if (analytics.getHighestScore() != null) {
                    %>

                        <%= analytics.getHighestScore() %>

                    <%
                        } else {
                    %>

                        -

                    <%
                        }
                    %>

                </div>

                <div class="stat-description">
                    Best submitted score
                </div>

            </div>


            <!-- Lowest Score -->

            <div class="stat">

                <div class="stat-label">
                    Lowest Score
                </div>

                <div class="stat-value">

                    <%
                        if (analytics.getLowestScore() != null) {
                    %>

                        <%= analytics.getLowestScore() %>

                    <%
                        } else {
                    %>

                        -

                    <%
                        }
                    %>

                </div>

                <div class="stat-description">
                    Lowest submitted score
                </div>

            </div>


            <!-- Average Percentage -->

            <div class="stat">

                <div class="stat-label">
                    Average Percentage
                </div>

                <div class="stat-value">

                    <%
                        if (analytics.getAveragePercentage() != null) {
                    %>

                        <%= analytics.getAveragePercentage()
                                .setScale(
                                        2,
                                        java.math.RoundingMode.HALF_UP
                                ) %>%

                    <%
                        } else {
                    %>

                        -

                    <%
                        }
                    %>

                </div>

                <div class="stat-description">
                    Average percentage of submitted attempts
                </div>

            </div>


            <!-- Pass Percentage -->

            <div class="stat">

                <div class="stat-label">
                    Pass Rate
                </div>

                <div class="stat-value">

                    <%
                        if (analytics.getPassPercentage() != null) {
                    %>

                        <%= analytics.getPassPercentage()
                                .setScale(
                                        2,
                                        java.math.RoundingMode.HALF_UP
                                ) %>%

                    <%
                        } else {
                    %>

                        -

                    <%
                        }
                    %>

                </div>

                <div class="stat-description">
                    Percentage of submitted attempts scoring 40% or more
                </div>

            </div>


        </div>

    </div>


    <div class="card">

        <div class="actions">

            <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/view?id=<%= quiz.getId() %>">
                Back to Quiz
            </a>

            |

            <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/attempts?quizId=<%= quiz.getId() %>">
                View Student Attempts
            </a>

        </div>

    </div>

</div>

</body>

</html>