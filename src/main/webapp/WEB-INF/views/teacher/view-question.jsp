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
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        View Question
    </title>

</head>

<body>

<h1>
    Quiz Question
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

<p>
    <strong>
        Question <%= question.getPosition() %>
    </strong>
</p>

<p>
    <%= question.getQuestionText() %>
</p>

<p>
    Marks:
    <%= question.getMarks() %>
</p>

<hr>
<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/options?questionId=<%= question.getId() %>">
        Manage Options
    </a>

</p>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/edit?id=<%= question.getId() %>">
        Edit Question
    </a>

</p>

<form method="post"
      action="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/delete"
      onsubmit="return confirm('Are you sure you want to delete this question?');">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">

    <input type="hidden"
           name="id"
           value="<%= question.getId() %>">

    <button type="submit">
        Delete Question
    </button>

</form>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions?quizId=<%= quiz.getId() %>">
        Back to Questions
    </a>

</p>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/view?id=<%= quiz.getId() %>">
        Back to Quiz
    </a>

</p>

</body>

</html>