
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="java.time.ZoneId" %>
<%@ page import="java.time.format.DateTimeFormatter" %>
<%@ page import="com.edusphere.model.LearningActivity" %>

<%
    List<LearningActivity> activities =
            (List<LearningActivity>) request.getAttribute("activities");

    DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm")
                    .withZone(ZoneId.systemDefault());
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>My Learning Activity | EduSphere</title>
    <link rel="stylesheet"
          href="<%= request.getContextPath() %>/assets/css/style.css">
</head>
<body>

<main class="container">
    <h1>My Learning Activity</h1>

    <p>Recent activity recorded while using EduSphere.</p>

    <p>
        <a href="<%= request.getContextPath() %>/student/dashboard">
            Back to Dashboard
        </a>
    </p>

    <% if (activities == null || activities.isEmpty()) { %>

        <p>No learning activity has been recorded yet.</p>

    <% } else { %>

        <table>
            <thead>
            <tr>
                <th>Activity</th>
                <th>Course ID</th>
                <th>Resource ID</th>
                <th>Date and Time</th>
            </tr>
            </thead>

            <tbody>
            <% for (LearningActivity activity : activities) { %>
                <tr>
                    <td>
                        <%= activity.getEventType() %>
                    </td>

                    <td>
                        <%= activity.getCourseId() == null
                                ? "—"
                                : activity.getCourseId() %>
                    </td>

                    <td>
                        <%= activity.getResourceId() == null
                                ? "—"
                                : activity.getResourceId() %>
                    </td>

                    <td>
                        <%= activity.getOccurredAt() == null
                                ? "—"
                                : formatter.format(activity.getOccurredAt()) %>
                    </td>
                </tr>
            <% } %>
            </tbody>
        </table>

    <% } %>
</main>

</body>
</html>
