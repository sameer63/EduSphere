<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.QuizAttempt" %>
<%@ page import="com.edusphere.model.QuizOption" %>
<%@ page import="com.edusphere.model.QuizQuestion" %>
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

    List<QuizQuestion> questions =
            (List<QuizQuestion>)
                    request.getAttribute("questions");

    Map<Long, List<QuizOption>> optionsByQuestion =
            (Map<Long, List<QuizOption>>)
                    request.getAttribute("optionsByQuestion");

    Map<Long, Long> selectedOptions =
            (Map<Long, Long>)
                    request.getAttribute("selectedOptions");
%>

<!DOCTYPE html>

<html>

<head>

    <meta charset="UTF-8">

    <title>
        <%= quiz.getTitle() %> - Attempt
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: #f5f5f5;
        }

        .container {
            max-width: 900px;
            margin: auto;
        }

        .header {
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 25px;
            margin-bottom: 25px;
        }

        .question {
            background: white;
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 25px;
            margin-bottom: 20px;
        }

        .question-text {
            font-size: 18px;
            font-weight: bold;
            margin-bottom: 15px;
        }

        .option {
            margin: 10px 0;
        }

        .option label {
            cursor: pointer;
        }

        .marks {
            color: #666;
            font-size: 14px;
            margin-bottom: 15px;
        }

        button {
            padding: 12px 22px;
            cursor: pointer;
        }

        .actions {
            margin-top: 25px;
            margin-bottom: 40px;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="header">

        <h1>
            <%= quiz.getTitle() %>
        </h1>

        <p>
            <strong>Course:</strong>
            <%= course.getTitle() %>
        </p>

        <p>
            <strong>Attempt:</strong>
            #<%= attempt.getAttemptNumber() %>
        </p>

        <p>
            <strong>Maximum Attempts:</strong>
            <%= quiz.getMaxAttempts() %>
        </p>

    </div>


    <form method="post"
          action="<%= request.getContextPath() %>/student/courses/quizzes/answer">

          <input type="hidden"
                 name="csrfToken"
                 value="<%= request.getAttribute("csrfToken") %>">

        <input
                type="hidden"
                name="attemptId"
                value="<%= attempt.getId() %>"
        >


        <%
            if (questions == null || questions.isEmpty()) {
        %>

            <div class="question">

                <p>
                    This quiz does not contain any questions yet.
                </p>

            </div>

        <%
            } else {

                int questionNumber = 1;

                for (QuizQuestion question : questions) {

                    List<QuizOption> options =
                            optionsByQuestion.get(
                                    question.getId()
                            );

                    Long selectedOptionId =
                            selectedOptions.get(
                                    question.getId()
                            );
        %>

            <div class="question">

                <div class="question-text">

                    <%= questionNumber %>.
                    <%= question.getQuestionText() %>

                </div>


                <div class="marks">

                    Marks:
                    <%= question.getMarks() %>

                </div>


                <%
                    if (options == null || options.isEmpty()) {
                %>

                    <p>
                        No options available for this question.
                    </p>

                <%
                    } else {

                        for (QuizOption option : options) {
                %>

                    <div class="option">

                        <label>

                            <input
                                    type="radio"
                                    name="question_<%= question.getId() %>"
                                    value="<%= option.getId() %>"

                                <%
                                    if (selectedOptionId != null
                                            && selectedOptionId.equals(
                                                    option.getId())) {
                                %>

                                    checked

                                <%
                                    }
                                %>
                            >

                            <%= option.getOptionText() %>

                        </label>

                    </div>

                <%
                        }
                    }
                %>

            </div>

        <%
                    questionNumber++;
                }
            }
        %>


        <div class="actions">

            <button type="submit">
                Save Answers
            </button>

        </div>

        </form>


        <div class="actions">

            <form method="post"
                  action="<%= request.getContextPath() %>/student/courses/quizzes/submit">

                  <input type="hidden"
                         name="csrfToken"
                         value="<%= request.getAttribute("csrfToken") %>">

                <input
                        type="hidden"
                        name="attemptId"
                        value="<%= attempt.getId() %>"
                >

                <button
                        type="submit"
                        onclick="return confirm('Are you sure you want to submit this quiz? You will not be able to change your answers afterwards.');">

                    Submit Quiz

                </button>

            </form>

        </div>

    </form>


    <div class="actions">

        <a href="<%= request.getContextPath() %>/student/courses/quizzes/view?id=<%= quiz.getId() %>">
            ← Back to Quiz
        </a>

    </div>

</div>

</body>

</html>