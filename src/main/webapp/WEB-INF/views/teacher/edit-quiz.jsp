<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>

<%
    Quiz quiz = (Quiz) request.getAttribute("quiz");
    Course course = (Course) request.getAttribute("course");

    String error = (String) request.getAttribute("error");

    String title = (String) request.getAttribute("title");
    String description =
            (String) request.getAttribute("description");
    String maxAttempts =
            (String) request.getAttribute("maxAttempts");

    if (title == null && quiz != null) {
        title = quiz.getTitle();
    }

    if (description == null && quiz != null) {
        description = quiz.getDescription();
    }

    if (maxAttempts == null && quiz != null) {
        maxAttempts = String.valueOf(
                quiz.getMaxAttempts()
        );
    }
%>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Quiz</title>
</head>

<body>

<h1>Edit Quiz</h1>

<% if (error != null) { %>
    <p style="color:red;">
        <%= error %>
    </p>
<% } %>

<% if (course != null) { %>
    <p>
        Course:
        <strong>
            <%= course.getTitle() %>
        </strong>
    </p>
<% } %>

<form method="post"
      action="<%= request.getContextPath() %>/teacher/courses/quizzes/edit">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">

    <input type="hidden"
           name="id"
           value="<%= quiz.getId() %>">

    <div>
        <label for="title">Quiz Title:</label>
        <br>

        <input type="text"
               id="title"
               name="title"
               value="<%= title != null ? title : "" %>"
               maxlength="200"
               required>
    </div>

    <br>

    <div>
        <label for="description">Description:</label>
        <br>

        <textarea id="description"
                  name="description"
                  rows="6"
                  cols="60"
                  maxlength="5000"><%= description != null ? description : "" %></textarea>
    </div>

    <br>

    <div>
        <label for="maxAttempts">
            Maximum Attempts:
        </label>
        <br>

        <input type="number"
               id="maxAttempts"
               name="maxAttempts"
               value="<%= maxAttempts != null ? maxAttempts : "" %>"
               min="1"
               max="100"
               required>
    </div>

    <br>

    <button type="submit">
        Update Quiz
    </button>

</form>

<p>
    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/view?id=<%= quiz.getId() %>">
        Cancel
    </a>
</p>

</body>
</html>