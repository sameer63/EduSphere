<%--
  Created by IntelliJ IDEA.
  User: sameer
  Date: 10/2/26
  Time: 11:35 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>Student Dashboard - EduSphere</title>
  </head>
  <body>

  <h1>Student Dashboard</h1>
  <p>
      Welcome,
      <strong>
          <%= session.getAttribute("userName") %>
      </strong>
  </p>

  <p>
      You are logged in as:
      <strong>
          <%= session.getAttribute("userRole") %>
      </strong>
  </p>
  <hr>
  <h2>Learning</h2>
  <ul>
    <li>My Courses</li>
    <li>My Progress</li>
    <li>Assignments</li>
    <li>Quizzes</li>
    <li>Results</li>
  </ul>
  <hr>
  <a href="<%= request.getContextPath() %>/student/courses">
      Browse Courses
  </a>
  <hr>
  <a href="<%= request.getContextPath() %>/student/enrolments">
      My Enrolled Courses
  </a>
  <hr>
  <a href="<%= request.getContextPath() %>/student/payments">
      My Payments
  </a>
  <hr>
  <a href="<%= request.getContextPath() %>/student/activity">
        My Activity
    </a>
<hr>
  <p>
      <a href="<%= request.getContextPath() %>/student/profile">
          My Profile
      </a>
  </p>
  <form
          method="post"
          action="<%= request.getContextPath() %>/logout"
  >

    <input type="hidden"
           name="csrfToken"
           value="<%= request.getAttribute("csrfToken") %>">

      <button type="submit">
          Logout
      </button>
  </form>

  </body>
</html>
