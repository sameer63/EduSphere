<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Assignment" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Submission" %>

<%
    Assignment assignment =
            (Assignment) request.getAttribute("assignment");

    Course course =
            (Course) request.getAttribute("course");

    Boolean deadlinePassed =
            (Boolean) request.getAttribute("deadlinePassed");

    String error =
            (String) request.getAttribute("error");
    Submission submission=(Submission) request.getAttribute("submission");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Submit Assignment - EduSphere
    </title>

</head>

<body>

<h1>Submit Assignment</h1>

<p>
    <strong>Course:</strong>
    <%= course.getTitle() %>
</p>

<p>
    <strong>Assignment:</strong>
    <%= assignment.getTitle() %>
</p>

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

<%
    if (error != null && !error.isBlank()) {
%>

    <p>
        <strong>Error:</strong>
        <%= error %>
    </p>

<%
    }
%>


<%
    if (Boolean.TRUE.equals(deadlinePassed)) {
%>

    <h3>
        Submission deadline has passed.
    </h3>

    <p>
        You can no longer submit this assignment.
    </p>

<%
    } else {
%>

    <form method="post"
          action="<%= request.getContextPath() %>/student/courses/assignments/submit">

          <input type="hidden"
                 name="csrfToken"
                 value="<%= request.getAttribute("csrfToken") %>">

        <input type="hidden"
               name="assignmentId"
               value="<%= assignment.getId() %>">

        <div>

            <label for="submissionText">
                Your Answer
            </label>

            <br>

            <textarea
                    id="submissionText"
                    name="submissionText"
                    rows="15"
                    cols="80"
                    maxlength="10000"
                    required></textarea>

        </div>

        <br>
<%
    if (submission == null) {
%>

    <button type="submit">
        Submit Assignment
    </button>

<%
    } else {
%>

    <p>
        <a href="<%= request.getContextPath() %>/student/courses/assignments/submission?id=<%= submission.getId() %>">
            View My Submission
        </a>
    </p>

<%
    }
%>

    </form>

<%
    }
%>

<hr>

<p>
    <a href="<%= request.getContextPath() %>/student/courses/assignments/view?id=<%= assignment.getId() %>">
        Back to Assignment
    </a>
</p>

<p>
    <a href="<%= request.getContextPath() %>/student/courses/assignments?courseId=<%= course.getId() %>">
        Back to Assignments
    </a>
</p>

</body>

</html>