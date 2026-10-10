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

    String error =
            (String) request.getAttribute("error");

    String submittedMarks =
            (String) request.getAttribute("submittedMarks");

    String submittedFeedback =
            (String) request.getAttribute("submittedFeedback");

    String marksValue;

    if (submittedMarks != null) {
        marksValue = submittedMarks;
    } else if (submission.getMarks() != null) {
        marksValue = submission.getMarks().toString();
    } else {
        marksValue = "";
    }

    String feedbackValue;

    if (submittedFeedback != null) {
        feedbackValue = submittedFeedback;
    } else if (submission.getFeedback() != null) {
        feedbackValue = submission.getFeedback();
    } else {
        feedbackValue = "";
    }
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        View Submission - EduSphere
    </title>

</head>

<body>

<h1>Student Submission</h1>

<p>
    <strong>Course:</strong>
    <%= course.getTitle() %>
</p>

<p>
    <strong>Assignment:</strong>
    <%= assignment.getTitle() %>
</p>

<p>
    <strong>Student ID:</strong>
    <%= submission.getStudentId() %>
</p>

<p>
    <strong>Submitted At:</strong>
    <%= submission.getSubmittedAt() %>
</p>

<hr>

<h2>Student's Answer</h2>

<%
    if (submission.getSubmissionText() != null &&
            !submission.getSubmissionText().isBlank()) {
%>

    <div>
        <%= submission.getSubmissionText() %>
    </div>

<%
    } else {
%>

    <p>
        No submission text.
    </p>

<%
    }
%>

<hr>

<h2>Grade Submission</h2>

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

<form method="post"
      action="<%= request.getContextPath() %>/teacher/courses/assignments/submissions/grade">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">

    <input type="hidden"
           name="submissionId"
           value="<%= submission.getId() %>">

    <p>

        <label for="marks">
            Marks
        </label>

        <br>

        <input type="number"
               id="marks"
               name="marks"
               value="<%= marksValue %>"
               min="0"
               max="<%= assignment.getMaxMarks() %>"
               step="0.01"
               required>

        /
        <%= assignment.getMaxMarks() %>

    </p>

    <p>

        <label for="feedback">
            Feedback
        </label>

        <br>

        <textarea
                id="feedback"
                name="feedback"
                rows="8"
                cols="70"
                maxlength="5000"><%= feedbackValue %></textarea>

    </p>

    <button type="submit">
        Save Grade
    </button>

</form>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/assignments/submissions?assignmentId=<%= assignment.getId() %>">

        Back to Submissions

    </a>

</p>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/assignments/view?id=<%= assignment.getId() %>">

        Back to Assignment

    </a>

</p>

</body>

</html>