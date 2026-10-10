<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Assignment" %>

<%
    Course course =
            (Course) request.getAttribute("course");

    List<Assignment> assignments =
            (List<Assignment>) request.getAttribute(
                    "assignments"
            );
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Course Assignments
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
            background: #f5f5f5;
        }

        .container {
            max-width: 850px;
            margin: auto;
        }

        h1 {
            margin-bottom: 5px;
        }

        .course-name {
            color: #666;
            margin-bottom: 25px;
        }

        .actions {
            margin-bottom: 25px;
        }

        .assignment {
            background: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 8px;
        }

        .assignment-title {
            font-size: 20px;
            font-weight: bold;
            margin-bottom: 10px;
        }

        .details {
            color: #666;
            margin-bottom: 10px;
        }

        .empty {
            background: white;
            padding: 20px;
            border-radius: 8px;
        }

        a {
            text-decoration: none;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>
        Course Assignments
    </h1>

    <div class="course-name">

        Course:
        <strong>
            <%= course.getTitle() %>
        </strong>

    </div>

    <div class="actions">

        <a href="<%= request.getContextPath() %>/teacher/courses/assignments/create?courseId=<%= course.getId() %>">
            + Create Assignment
        </a>

    </div>

    <% if (assignments == null ||
           assignments.isEmpty()) { %>

        <div class="empty">

            No assignments have been created
            for this course yet.

        </div>

    <% } else { %>

        <% for (Assignment assignment : assignments) { %>

            <div class="assignment">

                <div class="assignment-title">

                    <%= assignment.getTitle() %>

                </div>

                <div class="details">

                    Maximum Marks:
                    <strong>
                        <%= assignment.getMaxMarks() %>
                    </strong>

                </div>

                <div class="details">

                    Deadline:

                    <%
                        if (assignment.getDeadline() != null) {
                    %>

                        <%= assignment.getDeadline() %>

                    <%
                        } else {
                    %>

                        No deadline

                    <%
                        }
                    %>

                </div>

                <a href="<%= request.getContextPath() %>/teacher/courses/assignments/view?id=<%= assignment.getId() %>">

                    View Assignment →

                </a>

            </div>

        <% } %>

    <% } %>

    <br>

    <a href="<%= request.getContextPath() %>/teacher/courses/view?id=<%= course.getId() %>">

        ← Back to Course

    </a>

</div>

</body>

</html>