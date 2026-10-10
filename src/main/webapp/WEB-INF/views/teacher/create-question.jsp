<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>

<%
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
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Add Quiz Question
    </title>

</head>

<body>

<h1>
    Add Quiz Question
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
      action="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/create">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">

    <input type="hidden"
           name="quizId"
           value="<%= quiz.getId() %>">

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
                required><%= questionText != null ? questionText : "" %></textarea>

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
                value="<%= marks != null ? marks : "" %>"
                min="1"
                required>

    </div>

    <br>

    <div>

        <label for="position">
            Question Position:
        </label>

        <br>

        <input
                type="number"
                id="position"
                name="position"
                value="<%= position != null ? position : "" %>"
                min="1"
                required>

    </div>

    <br>

    <button type="submit">
        Create Question
    </button>

</form>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions?quizId=<%= quiz.getId() %>">
        Back to Questions
    </a>

</p>

</body>

</html>