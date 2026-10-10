<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Assignment" %>
<%@ page import="com.edusphere.model.Course" %>

<%
    Assignment assignment =
            (Assignment) request.getAttribute(
                    "assignment"
            );

    Course course =
            (Course) request.getAttribute(
                    "course"
            );
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        View Assignment
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

        h1 {
            margin-bottom: 25px;
        }

        .description {
            line-height: 1.7;
            white-space: pre-wrap;
            padding: 20px;
            background: #f8f8f8;
            border-radius: 5px;
        }

        .details {
            margin-top: 25px;
        }

        .detail {
            margin-bottom: 10px;
        }

        .actions {
            margin-top: 30px;
        }

        .actions a {
            margin-right: 15px;
        }

        .delete-form {
            display: inline;
        }

        button {
            cursor: pointer;
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

    <h1>
        <%= assignment.getTitle() %>
    </h1>

    <div class="description">

        <%
            if (assignment.getDescription() != null &&
                !assignment.getDescription().isBlank()) {
        %>

            <%= assignment.getDescription() %>

        <%
            } else {
        %>

            No description has been provided.

        <%
            }
        %>

    </div>

    <div class="details">

        <div class="detail">

            <strong>
                Maximum Marks:
            </strong>

            <%= assignment.getMaxMarks() %>

        </div>

        <div class="detail">

            <strong>
                Deadline:
            </strong>

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

        <div class="detail">

            <strong>
                Created:
            </strong>

            <%= assignment.getCreatedAt() %>

        </div>

    </div>

    <div class="actions">

        <a href="<%= request.getContextPath() %>/teacher/courses/assignments/submissions?assignmentId=<%= assignment.getId() %>">
            View Student Submissions
        </a>


        <a href="<%= request.getContextPath() %>/teacher/courses/assignments?courseId=<%= course.getId() %>">

            ← Back to Assignments

        </a>

        <a href="<%= request.getContextPath() %>/teacher/courses/assignments/edit?id=<%= assignment.getId() %>">

            Edit Assignment

        </a>

        <form method="post"
              class="delete-form"
              action="<%= request.getContextPath() %>/teacher/courses/assignments/delete"
              onsubmit="return confirm('Are you sure you want to delete this assignment?');">

              <input type="hidden"
                     name="csrfToken"
                     value="<%= request.getAttribute("csrfToken") %>">

            <input type="hidden"
                   name="id"
                   value="<%= assignment.getId() %>">

            <button type="submit">
                Delete Assignment
            </button>

        </form>

    </div>

</div>

</body>

</html>