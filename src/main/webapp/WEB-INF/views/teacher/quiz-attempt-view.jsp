<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.QuizAttempt" %>
<%@ page import="com.edusphere.dto.QuizAttemptQuestionResultDTO" %>
<%@ page import="java.math.BigDecimal" %>
<%@ page import="java.time.format.DateTimeFormatter" %>

<%
    Quiz quiz =
            (Quiz) request.getAttribute("quiz");

    Course course =
            (Course) request.getAttribute("course");

    QuizAttempt attempt =
            (QuizAttempt) request.getAttribute("attempt");

    List<QuizAttemptQuestionResultDTO> results =
            (List<QuizAttemptQuestionResultDTO>)
                    request.getAttribute("results");

    BigDecimal totalMarks =
            (BigDecimal) request.getAttribute("totalMarks");

    BigDecimal score =
            (BigDecimal) request.getAttribute("score");

    BigDecimal percentage =
            (BigDecimal) request.getAttribute("percentage");

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern(
                    "dd-MM-yyyy HH:mm:ss"
            );
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>Quiz Attempt Result</title>

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

        h2 {
            margin-top: 0;
        }

        .summary-grid {
            display: grid;
            grid-template-columns:
                    repeat(4, 1fr);
            gap: 15px;
        }

        .summary-item {
            border: 1px solid #ddd;
            padding: 15px;
            border-radius: 6px;
        }

        .summary-label {
            font-size: 13px;
            color: #666;
        }

        .summary-value {
            margin-top: 5px;
            font-size: 18px;
            font-weight: bold;
        }

        .question {
            border: 1px solid #ddd;
            padding: 20px;
            margin-bottom: 20px;
            border-radius: 6px;
        }

        .question-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 15px;
        }

        .question-text {
            font-size: 18px;
            font-weight: bold;
        }

        .marks {
            font-weight: bold;
        }

        .answer-box {
            padding: 12px;
            margin-top: 10px;
            border-radius: 5px;
        }

        .correct {
            background: #e8f5e9;
            border: 1px solid #81c784;
        }

        .incorrect {
            background: #ffebee;
            border: 1px solid #e57373;
        }

        .unanswered {
            background: #fff8e1;
            border: 1px solid #ffcc80;
        }

        .result {
            font-weight: bold;
            margin-top: 10px;
        }

        .correct-text {
            color: green;
        }

        .incorrect-text {
            color: red;
        }

        .unanswered-text {
            color: #e65100;
        }

        .actions {
            margin-top: 20px;
        }

        .actions a {
            margin-right: 15px;
        }

        @media (max-width: 800px) {

            .summary-grid {
                grid-template-columns:
                        repeat(2, 1fr);
            }
        }

    </style>

</head>

<body>

<div class="container">

    <div class="card">

        <h1>Quiz Attempt Result</h1>

        <p>
            <strong>Course:</strong>
            <%= course.getTitle() %>
        </p>

        <p>
            <strong>Quiz:</strong>
            <%= quiz.getTitle() %>
        </p>

        <p>
            <strong>Student ID:</strong>
            <%= attempt.getStudentId() %>
        </p>

        <p>
            <strong>Attempt Number:</strong>
            <%= attempt.getAttemptNumber() %>
        </p>

    </div>


    <!-- Result Summary -->

    <div class="card">

        <h2>Result Summary</h2>

        <div class="summary-grid">

            <div class="summary-item">

                <div class="summary-label">
                    Score
                </div>

                <div class="summary-value">
                    <%= score %>
                    /
                    <%= totalMarks %>
                </div>

            </div>


            <div class="summary-item">

                <div class="summary-label">
                    Percentage
                </div>

                <div class="summary-value">
                    <%= percentage %>%
                </div>

            </div>


            <div class="summary-item">

                <div class="summary-label">
                    Status
                </div>

                <div class="summary-value">

                    <%
                        if (attempt.getSubmittedAt() != null) {
                    %>

                        Submitted

                    <%
                        } else {
                    %>

                        In Progress

                    <%
                        }
                    %>

                </div>

            </div>


            <div class="summary-item">

                <div class="summary-label">
                    Attempt Number
                </div>

                <div class="summary-value">
                    <%= attempt.getAttemptNumber() %>
                </div>

            </div>

        </div>


        <br>

        <p>
            <strong>Started:</strong>

            <%= attempt.getStartedAt() != null
                    ? attempt.getStartedAt().format(formatter)
                    : "-" %>
        </p>


        <p>
            <strong>Submitted:</strong>

            <%= attempt.getSubmittedAt() != null
                    ? attempt.getSubmittedAt().format(formatter)
                    : "-" %>
        </p>

    </div>


    <!-- Question Results -->

    <div class="card">

        <h2>Question-wise Result</h2>

        <%
            if (results == null || results.isEmpty()) {
        %>

            <p>
                No questions found for this quiz.
            </p>

        <%
            } else {

                int questionNumber = 1;

                for (
                    QuizAttemptQuestionResultDTO result
                    : results
                ) {
        %>


            <div class="question">

                <div class="question-header">

                    <div class="question-text">

                        Question <%= questionNumber %>

                    </div>

                    <div class="marks">

                        <%= result.getMarksAwarded() %>
                        /
                        <%= result.getQuestionMarks() %>
                        marks

                    </div>

                </div>


                <p>

                    <strong>
                        <%= result.getQuestionText() %>
                    </strong>

                </p>


                <!-- Student Answer -->

                <%
                    if (result.isAnswered()) {
                %>

                    <div class="answer-box
                        <%= result.isCorrect()
                                ? "correct"
                                : "incorrect" %>">

                        <strong>
                            Student's Answer:
                        </strong>

                        <%= result.getSelectedOptionText() %>

                    </div>

                <%
                    } else {
                %>

                    <div class="answer-box unanswered">

                        <strong>
                            Student's Answer:
                        </strong>

                        Not answered

                    </div>

                <%
                    }
                %>


                <!-- Correct Answer -->

                <div class="answer-box correct">

                    <strong>
                        Correct Answer:
                    </strong>

                    <%
                        if (result.getCorrectOptionText() != null) {
                    %>

                        <%= result.getCorrectOptionText() %>

                    <%
                        } else {
                    %>

                        No correct option configured.

                    <%
                        }
                    %>

                </div>


                <!-- Result -->

                <div class="result">

                    <%
                        if (!result.isAnswered()) {
                    %>

                        <span class="unanswered-text">
                            Unanswered
                        </span>

                    <%
                        } else if (result.isCorrect()) {
                    %>

                        <span class="correct-text">
                            Correct
                        </span>

                    <%
                        } else {
                    %>

                        <span class="incorrect-text">
                            Incorrect
                        </span>

                    <%
                        }
                    %>

                </div>

            </div>


        <%
                    questionNumber++;
                }
            }
        %>

    </div>


    <!-- Navigation -->

    <div class="card">

        <div class="actions">

            <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/attempts?quizId=<%= quiz.getId() %>">
                Back to Student Attempts
            </a>

            |

            <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/view?id=<%= quiz.getId() %>">
                Back to Quiz
            </a>

        </div>

    </div>

</div>

</body>

</html>