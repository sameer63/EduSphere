<%--
  Created by IntelliJ IDEA.
  User: sameer
  Date: 10/3/26
  Time: 5:11 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.edusphere.dto.StudentProfile" %>

<%
    StudentProfile profile =
            (StudentProfile) request.getAttribute(
                    "profile"
            );
%>
<html>
  <head>
    <title>My Profile - EduSphere</title>
  </head>
  <body>
  <h1>My Profile</h1>
  <hr>
  <h2>Account Information</h2>

  <p>
      <strong>Name:</strong>
      <%= profile.getName() %>
  </p>
  <p>
      <strong>Email:</strong>
      <%= profile.getEmail() %>
  </p>

  <p>
      <strong>Date of Birth:</strong>
      <%= profile.getDateOfBirth() != null
              ? profile.getDateOfBirth()
              : "Not provided" %>
  </p>

  <p>
      <strong>Phone:</strong>
      <%= profile.getPhone() != null
              ? profile.getPhone()
              : "Not provided" %>
  </p>
  <hr>
  <p>
      <a href="<%= request.getContextPath() %>/student/dashboard">
          Back to Dashboard
      </a>

  </p>

  </body>
</html>
