<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>

<html>

<head>

    <title>Error</title>

</head>

<body>

    <h1>Something went wrong</h1>

    <%
        String error =
                (String) request.getAttribute("error");

        if (error != null) {
    %>

        <p>
            <%= error %>
        </p>

    <%
        }
    %>

    <a href="<%= request.getContextPath() %>/student/dashboard">
        Back to Dashboard
    </a>

</body>

</html>