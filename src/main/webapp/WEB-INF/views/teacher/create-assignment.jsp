<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Course" %>

<%
    Course course =
            (Course) request.getAttribute("course");

    String error =
            (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>Create Assignment</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        .container {
            max-width: 700px;
            margin: auto;
        }

        .course-name {
            color: #666;
            margin-bottom: 25px;
        }

        .error {
            background: #ffe5e5;
            color: #b00020;
            padding: 10px;
            margin-bottom: 20px;
            border-radius: 5px;
        }

        label {
            display: block;
            margin-top: 15px;
            margin-bottom: 5px;
            font-weight: bold;
        }

        input,
        textarea {
            width: 100%;
            padding: 10px;
            box-sizing: border-box;
        }

        textarea {
            min-height: 180px;
            resize: vertical;
        }

        button {
            margin-top: 20px;
            padding: 10px 18px;
            cursor: pointer;
        }

        .back {
            display: inline-block;
            margin-top: 20px;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>Create Assignment</h1>

    <% if (course != null) { %>

        <div class="course-name">

            Course:
            <strong>
                <%= course.getTitle() %>
            </strong>

        </div>

    <% } %>

    <% if (error != null) { %>

        <div class="error">
            <%= error %>
        </div>

    <% } %>

    <% if (course != null) { %>

        <form method="post"
              action="<%= request.getContextPath() %>/teacher/courses/assignments/create">
              <input type="hidden"
                     name="csrfToken"
                     value="<%= request.getAttribute("csrfToken") %>">

            <input type="hidden"
                   name="courseId"
                   value="<%= course.getId() %>">

            <label for="title">
                Assignment Title
            </label>

            <input type="text"
                   id="title"
                   name="title"
                   maxlength="200"
                   required>

            <label for="description">
                Description
            </label>

            <textarea id="description"
                      name="description"
                      placeholder="Describe the assignment..."></textarea>

            <label for="deadline">
                Deadline
            </label>

            <input type="datetime-local"
                   id="deadline"
                   name="deadline">

            <label for="maxMarks">
                Maximum Marks
            </label>

            <input type="number"
                   id="maxMarks"
                   name="maxMarks"
                   min="0.01"
                   step="0.01"
                   required>

            <button type="submit">
                Create Assignment
            </button>

        </form>

        <a class="back"
           href="<%= request.getContextPath() %>/teacher/courses/assignments?courseId=<%= course.getId() %>">

            ← Back to Assignments

        </a>

    <% } %>

</div>

</body>

</html>