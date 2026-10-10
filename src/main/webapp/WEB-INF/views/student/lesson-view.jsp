<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Lesson" %>
<%@ page import="com.edusphere.model.Course" %>

<%
    Lesson lesson =
            (Lesson) request.getAttribute("lesson");

    Course course =
            (Course) request.getAttribute("course");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        <%= lesson.getTitle() %>
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background: #f5f5f5;
        }

        .container {
            max-width: 850px;
            margin: 40px auto;
            background: white;
            padding: 35px;
            border-radius: 8px;
        }

        .course-name {
            color: #666;
            margin-bottom: 8px;
        }

        .position {
            color: #888;
            font-size: 14px;
            margin-bottom: 20px;
        }

        h1 {
            margin-bottom: 25px;
        }

        .content {
            line-height: 1.8;
            font-size: 17px;
            white-space: pre-wrap;
        }

        .navigation {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #ddd;
        }

        .navigation a {
            margin-right: 20px;
            text-decoration: none;
        }

        .metadata {
            margin-top: 30px;
            color: #888;
            font-size: 13px;
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

        Created:
        <%= lesson.getCreatedAt() %>

        <br>

        Last Updated:
        <%= lesson.getUpdatedAt() %>

    </div>

    <div class="navigation">

        <a href="<%= request.getContextPath() %>/student/courses/lessons?courseId=<%= course.getId() %>">

            ← Back to Lessons

        </a>

        <a href="<%= request.getContextPath() %>/student/enrolments">

            My Enrolled Courses

        </a>

    </div>

</div>

</body>

</html>