<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Course" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>

<html>

<head>

    <title>Browse Courses</title>

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

        .description {
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
            padding: 9px 15px;
            border: 1px solid #333;
            border-radius: 5px;
            text-decoration: none;
        }

        .empty {
            margin-top: 30px;
            padding: 20px;
            border: 1px solid #ddd;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="header">

        <h1>Browse Courses</h1>

        <a href="<%= request.getContextPath() %>/student/dashboard">
            Dashboard
        </a>

    </div>


    <%
        List<Course> courses =
                (List<Course>) request.getAttribute("courses");

        if (courses == null || courses.isEmpty()) {
    %>

        <div class="empty">

            <h2>No courses available</h2>

            <p>
                There are currently no published courses.
            </p>

        </div>

    <%
        } else {

            for (Course course : courses) {
    %>

        <div class="course-card">

            <div class="course-title">
                <%= course.getTitle() %>
            </div>


            <div class="description">

                <%
                    if (course.getDescription() != null
                            && !course.getDescription().isBlank()) {
                %>

                    <%= course.getDescription() %>

                <%
                    } else {
                %>

                    No description available.

                <%
                    }
                %>

            </div>


            <div class="course-info">

                <span>
                    <strong>Price:</strong>
                    ₹<%= course.getPrice() %>
                </span>

                <span>
                    <strong>Status:</strong>
                    <%= course.getStatus() %>
                </span>

            </div>


            <div class="actions">

                <a href="<%= request.getContextPath() %>/student/courses/view?id=<%= course.getId() %>">
                    View Course
                </a>

            </div>

        </div>

    <%
            }
        }
    %>

</div>

</body>

</html>