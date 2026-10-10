<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Lesson" %>

<%
    Course course =
            (Course) request.getAttribute("course");

    List<Lesson> lessons =
            (List<Lesson>) request.getAttribute("lessons");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Course Lessons
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
        }

        h1 {
            margin-bottom: 5px;
        }

        .course-name {
            color: #666;
            margin-bottom: 25px;
        }

        .lesson {
            background: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 8px;
        }

        .position {
            color: #777;
            font-size: 14px;
        }

        .lesson-title {
            font-size: 20px;
            font-weight: bold;
            margin: 8px 0;
        }

        .lesson a {
            text-decoration: none;
        }

        .empty {
            background: white;
            padding: 20px;
            border-radius: 8px;
        }

        .back {
            display: inline-block;
            margin-top: 20px;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>
        Course Lessons
    </h1>

    <div class="course-name">

        Course:
        <strong>
            <%= course.getTitle() %>
        </strong>

    </div>

    <% if (lessons == null || lessons.isEmpty()) { %>

        <div class="empty">
            No lessons are available for this course yet.
        </div>

    <% } else { %>

        <% for (Lesson lesson : lessons) { %>

            <div class="lesson">

                <div class="position">
                    Lesson <%= lesson.getPosition() %>
                </div>

                <div class="lesson-title">
                    <%= lesson.getTitle() %>
                </div>

                <a href="<%= request.getContextPath() %>/student/courses/lessons/view?id=<%= lesson.getId() %>">
                    Open Lesson →
                </a>

            </div>

        <% } %>

    <% } %>

    <a class="back"
       href="<%= request.getContextPath() %>/student/enrolments">

        ← Back to My Enrolled Courses

    </a>

</div>

</body>

</html>