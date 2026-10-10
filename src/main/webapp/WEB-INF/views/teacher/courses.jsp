<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Course" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>

<html>

<head>

    <title>My Courses</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        .container {
            max-width: 1100px;
            margin: auto;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .course-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin-top: 20px;
        }

        .course-title {
            font-size: 22px;
            font-weight: bold;
        }

        .course-description {
            margin-top: 10px;
            color: #555;
        }

        .course-info {
            margin-top: 15px;
        }

        .course-info span {
            margin-right: 25px;
        }

        .actions {
            margin-top: 20px;
        }

        .actions a {
            display: inline-block;
            padding: 8px 14px;
            margin-right: 10px;
            border: 1px solid #333;
            border-radius: 5px;
            text-decoration: none;
        }

        .empty {
            margin-top: 30px;
            padding: 20px;
            border: 1px solid #ddd;
        }

        .error {
            background: #ffe5e5;
            padding: 12px;
            margin-top: 20px;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="header">

        <h1>My Courses</h1>

        <a href="<%= request.getContextPath() %>/teacher/courses/create">
            + Create Course
        </a>

    </div>


    <%
        String error = (String) request.getAttribute("error");

        if (error != null) {
    %>

        <div class="error">
            <%= error %>
        </div>

    <%
        }
    %>


    <%
        List<Course> courses =
                (List<Course>) request.getAttribute("courses");

        if (courses == null || courses.isEmpty()) {
    %>

        <div class="empty">

            <h2>No courses yet</h2>

            <p>
                You have not created any courses yet.
            </p>

            <a href="<%= request.getContextPath() %>/teacher/courses/create">
                Create your first course
            </a>

        </div>

    <%
        } else {

            for (Course course : courses) {
    %>

        <div class="course-card">

            <div class="course-title">
                <%= course.getTitle() %>
            </div>


            <%
                if (course.getDescription() != null) {
            %>

                <div class="course-description">
                    <%= course.getDescription() %>
                </div>

            <%
                }
            %>


            <div class="course-info">

                <span>
                    <strong>Price:</strong>
                    ₹<%= course.getPrice() %>
                </span>

                <span>
                    <strong>Status:</strong>
                    <%= course.getStatus() %>
                </span>

                <%
                    if (course.getCreatedAt() != null) {
                %>

                    <span>
                        <strong>Created:</strong>
                        <%= course.getCreatedAt() %>
                    </span>

                <%
                    }
                %>

            </div>


            <div class="actions">

                <a href="<%= request.getContextPath() %>/teacher/courses/view?id=<%= course.getId() %>">
                    View
                </a>

                <a href="<%= request.getContextPath() %>/teacher/courses/edit?id=<%= course.getId() %>">
                    Edit
                </a>

            </div>

        </div>

    <%
            }
        }
    %>


    <br>

    <a href="<%= request.getContextPath() %>/teacher/dashboard">
        ← Back to Dashboard
    </a>

</div>

</body>

</html>