<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Lesson" %>
<%@ page import="com.edusphere.model.Course" %>

<%
    Lesson lesson = (Lesson) request.getAttribute("lesson");
    Course course = (Course) request.getAttribute("course");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        View Lesson
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: #f5f5f5;
        }

        .container {
            max-width: 800px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
        }

        .course-name {
            color: #666;
            margin-bottom: 10px;
        }

        .position {
            color: #777;
            margin-bottom: 20px;
        }

        h1 {
            margin-bottom: 20px;
        }

        .content {
            line-height: 1.7;
            white-space: pre-wrap;
            padding: 20px;
            background: #f8f8f8;
            border-radius: 5px;
        }

        .metadata {
            margin-top: 25px;
            color: #777;
            font-size: 14px;
        }

        .actions {
            margin-top: 30px;
        }

        .actions a {
            margin-right: 15px;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="course-name">

        Course:
        <strong>
            <%= course.getTitle() %>
        </strong>

    </div>

    <div class="position">

        Lesson <%= lesson.getPosition() %>

    </div>

    <h1>
        <%= lesson.getTitle() %>
    </h1>

    <div class="content">

        <%
            if (lesson.getContent() != null &&
                !lesson.getContent().isBlank()) {
        %>

            <%= lesson.getContent() %>

        <%
            } else {
        %>

            No content has been added to this lesson yet.

        <%
            }
        %>

    </div>

    <div class="metadata">

        <p>
            Created:
            <%= lesson.getCreatedAt() %>
        </p>

        <p>
            Last Updated:
            <%= lesson.getUpdatedAt() %>
        </p>

    </div>

    <div class="actions">

        <a href="<%= request.getContextPath() %>/teacher/courses/lessons?courseId=<%= course.getId() %>">
            ← Back to Lessons
        </a>

        <a href="<%= request.getContextPath() %>/teacher/courses/lessons/edit?id=<%= lesson.getId() %>">
            Edit Lesson
        </a>

        <form method="post"
              action="<%= request.getContextPath() %>/teacher/courses/lessons/delete"
              style="display:inline;"
              onsubmit="return confirm('Are you sure you want to delete this lesson?');">

              <input type="hidden"
                     name="csrfToken"
                     value="<%= request.getAttribute("csrfToken") %>">

            <input type="hidden"
                   name="id"
                   value="<%= lesson.getId() %>">

            <button type="submit">
                Delete Lesson
            </button>

        </form>

    </div>

</div>

</body>

</html>