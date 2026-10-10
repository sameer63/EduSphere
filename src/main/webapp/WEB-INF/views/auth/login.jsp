<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login - EduSphere</title>
</head>

<body>
<h1>Login to EduSphere</h1>
<%
    String error =
            (String) request.getAttribute("error");
    String email =
            (String) request.getAttribute("email");
    String registered =
            request.getParameter("registered");
%>
<% if (registered != null) { %>
    <p style="color: green;">
        Registration successful.
        You can now log in.
    </p>
<% } %>

<% if (error != null) { %>
    <p style="color: red;">
        <%= error %>
    </p>
<% } %>

<form
        method="post"
        action="<%= request.getContextPath() %>/login"
>

<input type="hidden"
       name="csrfToken"
       value="<%= request.getAttribute("csrfToken") %>">
    <div>
        <label for="email">
            Email
        </label>
        <input
                type="email"
                id="email"
                name="email"
                value="<%= email != null ? email : "" %>"
                required
        >
    </div>

    <br>

    <div>
        <label for="password">
            Password
        </label>
        <input
                type="password"
                id="password"
                name="password"
                required
        >
    </div>

    <br>
    <button type="submit">
        Login
    </button>
</form>

<p>
    Don't have an account?

    <a href="<%= request.getContextPath() %>/register">
        Register
    </a>
</p>
</body>

</html>