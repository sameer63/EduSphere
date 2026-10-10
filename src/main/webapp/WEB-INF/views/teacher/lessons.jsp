<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Lesson" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%
    Course course =
            (Course) request.getAttribute("course");

    List<Lesson> lessons =
            (List<Lesson>) request.getAttribute("lessons");
%>

<!DOCTYPE html>

<html>

<head>

    <title>
        Lessons - <%= course.getTitle() %>
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        .container {
            max-width: 900px;
            margin: auto;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .lesson-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin-top: 20px;
        }

        .position {
            font-weight: bold;
        }

        .lesson-title {
            font-size: 22px;
            margin-top: 8px;
        }

        .lesson-content {
            margin-top: 12px;
            color: #555;
        }

        .actions {
            margin-top: 25px;
        }

        .actions a {
            display: inline-block;
            padding: 9px 14px;
            margin-right: 10px;
            border: 1px solid #333;
            border-radius: 5px;
            text-decoration: none;
        }

        .empty {
            border: 1px solid #ddd;
            padding: 25px;
            margin-top: 30px;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="header">

        <div>

            <h1>Course Lessons</h1>

            <h2>
                <%= course.getTitle() %>
            </h2>

        </div>

        <a href="<%= request.getContextPath() %>/teacher/courses">
            My Courses
        </a>

    </div>


    <div class="actions">

        <a href="<%= request.getContextPath() %>/teacher/courses/lessons/create?courseId=<%= course.getId() %>">
            + Create Lesson
        </a>

    </div>


    <%
        if (lessons == null || lessons.isEmpty()) {
    %>

        <div class="empty">

            <h2>No lessons yet</h2>

            <p>
                This course does not have any lessons yet.
            </p>

        </div>

    <%
        } else {

            for (Lesson lesson : lessons) {
    %>

        <div class="lesson-card">

            <div class="position">

                Lesson <%= lesson.getPosition() %>

            </div>


            <div class="lesson-title">

                <%= lesson.getTitle() %>

            </div>


            <%
                if (lesson.getContent() != null
                        && !lesson.getContent().isBlank()) {
            %>

                <div class="lesson-content">

                    <%= lesson.getContent() %>

                </div>

            <%
                }
            %>


            <div class="actions">

                <a href="<%= request.getContextPath() %>/teacher/courses/lessons/view?id=<%= lesson.getId() %>">
                    View Lesson
                </a>

            </div>

        </div>

    <%
            }
        }
    %>


    <div class="actions">

        <a href="<%= request.getContextPath() %>/teacher/courses/view?id=<%= course.getId() %>">
            ← Back to Course
        </a>

    </div>

</div>

</body>

</html>