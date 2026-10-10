<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>

    <meta charset="UTF-8">

    <title>Register - EduSphere</title>

</head>

<body>

<h1>Create your EduSphere account</h1>

<%
    String error =
            (String) request.getAttribute("error");
%>

<% if (error != null) { %>

    <p style="color: red;">
        <%= error %>
    </p>

<% } %>

<form
        method="post"
        action="<%= request.getContextPath() %>/register"
>
<input type="hidden"
       name="csrfToken"
       value="<%= request.getAttribute("csrfToken") %>">
    <div>
        <label for="name">
            Name
        </label>
        <input
                type="text"
                id="name"
                name="name"
                value="<%= request.getAttribute("name") != null
                        ? request.getAttribute("name")
                        : "" %>"
                required
        >
    </div>

    <br>

    <div>
        <label for="email">
            Email
        </label>
        <input
                type="email"
                id="email"
                name="email"
                value="<%= request.getAttribute("email") != null
                        ? request.getAttribute("email")
                        : "" %>"
                required
        >
    </div>
    <br>
    <div>

        <label for="dateOfBirth">
            Date of Birth
        </label>

        <input
                type="date"
                id="dateOfBirth"
                name="dateOfBirth"
        >

    </div>

    <br>

    <div>
        <label for="phone">
            Phone
        </label>
        <input
                type="tel"
                id="phone"
                name="phone"
        >
    </div>
    <br>

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
        Register
    </button>
</form>
<p>
    Already have an account?
    <a href="<%= request.getContextPath() %>/login">
        Login
    </a>
</p>

</body>
</html>