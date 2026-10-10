<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Enrolment" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>

<html>

<head>

    <title>My Enrolled Courses</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        .container {
            max-width: 1000px;
            margin: auto;
        }

        .header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .enrolment-card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 20px;
            margin-top: 20px;
        }

        .course-id {
            font-size: 20px;
            font-weight: bold;
        }

        .info {
            margin-top: 12px;
        }

        .empty {
            border: 1px solid #ddd;
            padding: 25px;
            margin-top: 30px;
        }

        .actions {
            margin-top: 25px;
        }

        .actions a {
            margin-right: 15px;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="header">

        <h1>My Enrolled Courses</h1>

        <a href="<%= request.getContextPath() %>/student/dashboard">
            Dashboard
        </a>

    </div>


    <%
        String error =
                (String) request.getAttribute("error");

        if (error != null) {
    %>

        <p>
            <strong>Error:</strong>
            <%= error %>
        </p>

    <%
        }
    %>


    <%
        List<Enrolment> enrolments =
                (List<Enrolment>)
                        request.getAttribute("enrolments");

        if (enrolments == null ||
                enrolments.isEmpty()) {
    %>

        <div class="empty">

            <h2>No enrolled courses</h2>

            <p>
                You have not enrolled in any courses yet.
            </p>

            <a href="<%= request.getContextPath() %>/student/courses">
                Browse Courses
            </a>

        </div>

    <%
        } else {

            for (Enrolment enrolment :
                    enrolments) {
    %>

        <div class="enrolment-card">

            <div class="course-id">

                Course ID:
                <%= enrolment.getCourseId() %>

            </div>


            <div class="info">

                <strong>Enrolment ID:</strong>

                <%= enrolment.getId() %>

            </div>


            <div class="info">

                <strong>Status:</strong>

                <%= enrolment.getStatus() %>

            </div>


            <%
                if (enrolment.getEnrolledAt() != null) {
            %>

                <div class="info">

                    <strong>Enrolled At:</strong>

                    <%= enrolment.getEnrolledAt() %>

                </div>

            <%
                }
            %>


            <%
                if (enrolment.getCompletedAt() != null) {
            %>

                <div class="info">

                    <strong>Completed At:</strong>

                    <%= enrolment.getCompletedAt() %>

                </div>

            <%
                }
            %>


            <div class="actions">

                <a href="<%= request.getContextPath() %>/student/courses/view?id=<%= enrolment.getCourseId() %>">
                    View Course
                </a>

               <a href="<%= request.getContextPath() %>/student/courses/lessons?courseId=<%= enrolment.getCourseId() %>">
                                            View Lessons
                                        </a>

                <a href="<%= request.getContextPath() %>/student/courses/assignments?courseId=<%= enrolment.getCourseId() %>">
                                            View Assignments
                                        </a>


            </div>

        </div>

    <%
            }
        }
    %>


    <div class="actions">

        <a href="<%= request.getContextPath() %>/student/courses">
            Browse More Courses
        </a>

    </div>

</div>

</body>

</html>