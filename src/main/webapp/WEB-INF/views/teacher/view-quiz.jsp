<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>

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
        <%= quiz.getTitle() %> - EduSphere
    </title>

</head>

<body>

<h1>
    <%= quiz.getTitle() %>
</h1>

<p>
    <strong>Course:</strong>
    <%= course.getTitle() %>
</p>

<p>
    <strong>Maximum Attempts:</strong>
    <%= quiz.getMaxAttempts() %>
</p>

<hr>

<h3>Description</h3>

<%
    if (quiz.getDescription() != null &&
            !quiz.getDescription().isBlank()) {
%>

    <p>
        <%= quiz.getDescription() %>
    </p>

<%
    } else {
%>

    <p>
        No description provided.
    </p>

<%
    }
%>

<hr>

<h3>Quiz Management</h3>

<p>
    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions?quizId=<%= quiz.getId() %>">
        Manage Questions
    </a>
</p>

<p>
    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/edit?id=<%= quiz.getId() %>">
        Edit Quiz
    </a>
</p>

<form method="post"
      action="<%= request.getContextPath() %>/teacher/courses/quizzes/delete"
      onsubmit="return confirm('Are you sure you want to delete this quiz?');">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">

    <input type="hidden"
           name="id"
           value="<%= quiz.getId() %>">

    <button type="submit">
        Delete Quiz
    </button>

</form>
<br/>
<a href="<%= request.getContextPath() %>/teacher/courses/quizzes/attempts?quizId=<%= quiz.getId() %>">
    View Student Attempts
</a>
<br/>
<a href="<%= request.getContextPath() %>/teacher/courses/quizzes/analytics?quizId=<%= quiz.getId() %>">
    Quiz Analytics
</a>

<hr>

<p>
    Questions will be managed here.
</p>

<p>
    <strong>Next:</strong>
    Add Questions
</p>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes?courseId=<%= course.getId() %>">

        Back to Quizzes

    </a>

</p>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/view?id=<%= course.getId() %>">

        Back to Course

    </a>

</p>

</body>

</html>