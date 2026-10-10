<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Course" %>

<%
    Course course =
            (Course) request.getAttribute("course");

    String error =
            (String) request.getAttribute("error");

    String title =
            (String) request.getAttribute("title");

    String description =
            (String) request.getAttribute("description");

    String maxAttempts =
            (String) request.getAttribute("maxAttempts");

    if (title == null) {
        title = "";
    }

    if (description == null) {
        description = "";
    }

    if (maxAttempts == null) {
        maxAttempts = "1";
    }
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Create Quiz - EduSphere
    </title>

</head>

<body>

<h1>Create Quiz</h1>

<p>
    <strong>Course:</strong>
    <%= course.getTitle() %>
</p>

<hr>

<%
    if (error != null && !error.isBlank()) {
%>

    <p>
        <strong>Error:</strong>
        <%= error %>
    </p>

<%
    }
%>

<form method="post"
      action="<%= request.getContextPath() %>/teacher/courses/quizzes/create">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">

    <input type="hidden"
           name="courseId"
           value="<%= course.getId() %>">


    <p>

        <label for="title">
            Quiz Title
        </label>

        <br>

        <input type="text"
               id="title"
               name="title"
               value="<%= title %>"
               maxlength="200"
               required>

    </p>


    <p>

        <label for="description">
            Description
        </label>

        <br>

        <textarea
                id="description"
                name="description"
                rows="8"
                cols="70"
                maxlength="5000"><%= description %></textarea>

    </p>


    <p>

        <label for="maxAttempts">
            Maximum Attempts
        </label>

        <br>

        <input type="number"
               id="maxAttempts"
               name="maxAttempts"
               value="<%= maxAttempts %>"
               min="1"
               max="100"
               required>

    </p>


    <button type="submit">
        Create Quiz
    </button>

</form>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/view?id=<%= course.getId() %>">

        Back to Course

    </a>

</p>

</body>

</html>