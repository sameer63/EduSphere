<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="com.edusphere.model.Submission" %>
<%@ page import="com.edusphere.model.Assignment" %>
<%@ page import="com.edusphere.model.Course" %>

<%
    Submission submission =
            (Submission) request.getAttribute("submission");

    Assignment assignment =
            (Assignment) request.getAttribute("assignment");

    Course course =
            (Course) request.getAttribute("course");

    Boolean deadlinePassed =
            (Boolean) request.getAttribute("deadlinePassed");

    String error =
            (String) request.getAttribute("error");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Edit Submission - EduSphere
    </title>

</head>

<body>

<h1>Edit Submission</h1>

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

    <h3>Submission deadline has passed.</h3>

    <p>
        You can no longer modify your submission.
    </p>

<%
    } else {
%>

    <form method="post"
          action="<%= request.getContextPath() %>/student/courses/assignments/submission/edit">

          <input type="hidden"
                 name="csrfToken"
                 value="<%= request.getAttribute("csrfToken") %>">

        <input type="hidden"
               name="submissionId"
               value="<%= submission.getId() %>">

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
                required><%= submission.getSubmissionText() != null
                        ? submission.getSubmissionText()
                        : "" %></textarea>

        <br><br>

        <button type="submit">
            Resubmit Assignment
        </button>

    </form>

<%
    }
%>

<hr>

<p>
    <a href="<%= request.getContextPath() %>/student/courses/assignments/submission?id=<%= submission.getId() %>">
        Back to My Submission
    </a>
</p>

</body>

</html>