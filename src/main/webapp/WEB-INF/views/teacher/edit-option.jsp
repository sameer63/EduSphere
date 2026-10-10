<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.QuizOption" %>
<%@ page import="com.edusphere.model.QuizQuestion" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>

<%
    QuizOption option =
            (QuizOption) request.getAttribute("option");

    QuizQuestion question =
            (QuizQuestion) request.getAttribute("question");

    Quiz quiz =
            (Quiz) request.getAttribute("quiz");

    Course course =
            (Course) request.getAttribute("course");

    String error =
            (String) request.getAttribute("error");

    String optionText =
            (String) request.getAttribute("optionText");

    String correct =
            (String) request.getAttribute("correct");

    if (optionText == null) {
        optionText = option.getOptionText();
    }

    if (correct == null) {
        correct = String.valueOf(option.isCorrect());
    }
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Edit Quiz Option
    </title>

</head>

<body>

<h1>
    Edit Quiz Option
</h1>

<p>
    Course:
    <strong>
        <%= course.getTitle() %>
    </strong>
</p>

<p>
    Quiz:
    <strong>
        <%= quiz.getTitle() %>
    </strong>
</p>

<p>
    Question:
    <strong>
        <%= question.getQuestionText() %>
    </strong>
</p>

<hr>

<% if (error != null) { %>

    <p style="color:red;">
        <%= error %>
    </p>

<% } %>

<form method="post"
      action="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/options/edit">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">

    <input type="hidden"
           name="id"
           value="<%= option.getId() %>">

    <div>

        <label for="optionText">
            Option:
        </label>

        <br>

        <textarea
                id="optionText"
                name="optionText"
                rows="4"
                cols="60"
                maxlength="1000"
                required><%= optionText %></textarea>

    </div>

    <br>

    <div>

        <label>

            <input
                    type="checkbox"
                    name="correct"
                    value="true"
                    <%= "true".equalsIgnoreCase(correct)
                            ? "checked"
                            : "" %>>

            This is the correct answer

        </label>

    </div>

    <br>

    <button type="submit">
        Update Option
    </button>

</form>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/options?questionId=<%= question.getId() %>">
        Cancel
    </a>

</p>

</body>

</html>