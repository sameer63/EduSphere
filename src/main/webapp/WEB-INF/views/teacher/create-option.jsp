<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.QuizQuestion" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>

<%
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
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Add Quiz Option
    </title>

</head>

<body>

<h1>
    Add Quiz Option
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

<hr>

<h3>
    Question
</h3>

<p>
    <%= question.getQuestionText() %>
</p>

<hr>

<% if (error != null) { %>

    <p style="color:red;">
        <%= error %>
    </p>

<% } %>

<form method="post"
      action="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/options/create">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">

    <input type="hidden"
           name="questionId"
           value="<%= question.getId() %>">

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
                required><%= optionText != null ? optionText : "" %></textarea>

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
        Create Option
    </button>

</form>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/options?questionId=<%= question.getId() %>">
        Back to Options
    </a>

</p>

</body>

</html>