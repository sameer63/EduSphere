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
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        My Submission - EduSphere
    </title>

</head>

<body>

<h1>My Submission</h1>

<p>
    <strong>Course:</strong>
    <%= course.getTitle() %>
</p>

<p>
    <strong>Assignment:</strong>
    <%= assignment.getTitle() %>
</p>

<hr>

<h3>Your Answer</h3>

<%
    if (submission.getSubmissionText() != null &&
            !submission.getSubmissionText().isBlank()) {
%>

    <p>
        <%= submission.getSubmissionText() %>
    </p>

<%
    } else {
%>

    <p>
        No submission text available.
    </p>

<%
    }
%>

<hr>

<h3>Submission Information</h3>

<p>
    <strong>Submitted At:</strong>

    <%
        if (submission.getSubmittedAt() != null) {
    %>

        <%= submission.getSubmittedAt() %>

    <%
        } else {
    %>

        Not submitted

    <%
        }
    %>
</p>

<p>
    <strong>Maximum Marks:</strong>
    <%= assignment.getMaxMarks() %>
</p>

<hr>

<h3>Result</h3>

<%
    if (submission.getMarks() == null) {
%>

    <p>
        <strong>Status:</strong>
        Not graded yet.
    </p>

<%
    } else {
%>

    <p>
        <strong>Marks:</strong>
        <%= submission.getMarks() %>
        /
        <%= assignment.getMaxMarks() %>
    </p>

<%
    }
%>

<h3>Teacher Feedback</h3>

<%
    if (submission.getFeedback() == null ||
            submission.getFeedback().isBlank()) {
%>

    <p>
        No feedback available yet.
    </p>

<%
    } else {
%>

    <p>
        <%= submission.getFeedback() %>
    </p>

<%
    }
%>

<hr>

<%
    boolean deadlinePassed =
            assignment.getDeadline() != null &&
            java.time.LocalDateTime.now()
                    .isAfter(assignment.getDeadline());
%>

<%
    boolean canEdit =
            !deadlinePassed &&
            submission.getMarks() == null;

    if (canEdit) {
%>

    <p>
        <a href="<%= request.getContextPath() %>/student/courses/assignments/submission/edit?id=<%= submission.getId() %>">
            Edit / Resubmit
        </a>
    </p>

<%
    }
%>

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

<p>
    <a href="<%= request.getContextPath() %>/student/enrolments">
        My Enrolled Courses
    </a>
</p>

</body>

</html>