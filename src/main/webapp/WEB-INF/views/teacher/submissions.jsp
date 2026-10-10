<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Submission" %>
<%@ page import="com.edusphere.model.Assignment" %>
<%@ page import="com.edusphere.model.Course" %>

<%
    Assignment assignment =
            (Assignment) request.getAttribute("assignment");

    Course course =
            (Course) request.getAttribute("course");

    List<Submission> submissions =
            (List<Submission>) request.getAttribute("submissions");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Submissions - EduSphere
    </title>

</head>

<body>

<h1>Assignment Submissions</h1>

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

<hr>

<%
    if (submissions == null ||
            submissions.isEmpty()) {
%>

    <h3>No submissions yet.</h3>

    <p>
        No student has submitted this assignment.
    </p>

<%
    } else {
%>

    <h2>
        Submitted Students
    </h2>

    <table border="1"
           cellpadding="8"
           cellspacing="0">

        <thead>

        <tr>

            <th>
                Submission ID
            </th>

            <th>
                Student ID
            </th>

            <th>
                Submitted At
            </th>

            <th>
                Marks
            </th>

            <th>
                Status
            </th>

            <th>
                Action
            </th>

        </tr>

        </thead>

        <tbody>

        <%
            for (Submission submission : submissions) {
        %>

            <tr>

                <td>
                    <%= submission.getId() %>
                </td>

                <td>
                    <%= submission.getStudentId() %>
                </td>

                <td>
                    <%= submission.getSubmittedAt() %>
                </td>

                <td>

                    <%
                        if (submission.getMarks() == null) {
                    %>

                        Not graded

                    <%
                        } else {
                    %>

                        <%= submission.getMarks() %>
                        /
                        <%= assignment.getMaxMarks() %>

                    <%
                        }
                    %>

                </td>

                <td>

                    <%
                        if (submission.getMarks() == null) {
                    %>

                        Pending

                    <%
                        } else {
                    %>

                        Graded

                    <%
                        }
                    %>

                </td>

                <td>

                    <a href="<%= request.getContextPath() %>/teacher/courses/assignments/submissions/view?id=<%= submission.getId() %>">

                        View Submission

                    </a>

                </td>

            </tr>

        <%
            }
        %>

        </tbody>

    </table>

<%
    }
%>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/assignments/view?id=<%= assignment.getId() %>">

        Back to Assignment

    </a>

</p>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/view?id=<%= course.getId() %>">

        Back to Course

    </a>

</p>

</body>

</html>