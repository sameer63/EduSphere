<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Assignment" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Submission" %>

<%
    Assignment assignment =
            (Assignment) request.getAttribute("assignment");

    Course course =
            (Course) request.getAttribute("course");

    Submission submission =
               (Submission) request.getAttribute("submission");

%>

<!DOCTYPE html>
<html>
<head>

    <meta charset="UTF-8">

    <title>
        <%= assignment.getTitle() %> - EduSphere
    </title>

</head>

<body>

<h1>Assignment Details</h1>

<p>
    <strong>Course:</strong>
    <%= course.getTitle() %>
</p>

<hr>

<h2>
    <%= assignment.getTitle() %>
</h2>

<p>
    <strong>Maximum Marks:</strong>
    <%= assignment.getMaxMarks() %>
</p>

<p>
    <strong>Deadline:</strong>

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
</p>

<hr>

<h3>Description</h3>

<%
    if (assignment.getDescription() != null
            && !assignment.getDescription().isBlank()) {
%>

    <p>
        <%= assignment.getDescription() %>
    </p>

<%
    } else {
%>

    <p>
        No description provided.
    </p>

<%
    }
%>

<hr>

<h3>Actions</h3>

<p>
    <a href="<%= request.getContextPath() %>/student/courses/assignments/submit?id=<%= assignment.getId() %>">
        Submit Assignment
    </a>
</p>

<p>
    <a href="<%= request.getContextPath() %>/student/courses/assignments?courseId=<%= course.getId() %>">
        Back to Assignments
    </a>
</p>

<p>
    <a href="<%= request.getContextPath() %>/student/enrolments">
        Back to My Enrolled Courses
    </a>
</p>

</body>
</html>