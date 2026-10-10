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

    String questionText =
            (String) request.getAttribute("questionText");

    String marks =
            (String) request.getAttribute("marks");

    String position =
            (String) request.getAttribute("position");

    if (questionText == null) {
        questionText = question.getQuestionText();
    }

    if (marks == null) {
        marks = String.valueOf(question.getMarks());
    }

    if (position == null) {
        position = String.valueOf(question.getPosition());
    }
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Edit Question
    </title>

</head>

<body>

<h1>
    Edit Quiz Question
</h1>

<% if (error != null) { %>

    <p style="color:red;">
        <%= error %>
    </p>

<% } %>

<p>
    Quiz:
    <strong>
        <%= quiz.getTitle() %>
    </strong>
</p>

<p>
    Course:
    <strong>
        <%= course.getTitle() %>
    </strong>
</p>

<hr>

<form method="post"
      action="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/edit">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">

    <input type="hidden"
           name="id"
           value="<%= question.getId() %>">

    <div>

        <label for="questionText">
            Question:
        </label>

        <br>

        <textarea
                id="questionText"
                name="questionText"
                rows="6"
                cols="70"
                maxlength="5000"
                required><%= questionText %></textarea>

    </div>

    <br>

    <div>

        <label for="marks">
            Marks:
        </label>

        <br>

        <input
                type="number"
                id="marks"
                name="marks"
                value="<%= marks %>"
                min="1"
                required>

    </div>

    <br>

    <div>

        <label for="position">
            Position:
        </label>

        <br>

        <input
                type="number"
                id="position"
                name="position"
                value="<%= position %>"
                min="1"
                required>

    </div>

    <br>

    <button type="submit">
        Update Question
    </button>

</form>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/view?id=<%= question.getId() %>">
        Cancel
    </a>

</p>

</body>

</html>