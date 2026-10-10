<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>
<head>
    <title>Teacher Dashboard</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        .container {
            max-width: 900px;
            margin: auto;
        }

        .card {
            border: 1px solid #ddd;
            padding: 20px;
            margin-top: 20px;
            border-radius: 8px;
        }

        .actions a {
            display: inline-block;
            margin: 10px 10px 10px 0;
            padding: 10px 16px;
            text-decoration: none;
            border: 1px solid #333;
            border-radius: 5px;
        }

        .logout {
            margin-top: 30px;
        }
    </style>
</head>

<body>

<div class="container">

    <h1>Teacher Dashboard</h1>

    <div class="card">

        <h2>
            Welcome,
            <%= session.getAttribute("userName") %>
        </h2>

        <p>
            Role:
            <strong>
                <%= session.getAttribute("userRole") %>
            </strong>
        </p>

    </div>

    <div class="card">

        <h2>Course Management</h2>

        <div class="actions">

            <a href="<%= request.getContextPath() %>/teacher/courses/create">
                Create Course
            </a>

            <a href="<%= request.getContextPath() %>/teacher/courses">
                My Courses
            </a>

        </div>

    </div>

    <div class="card">

        <h2>Other</h2>

        <div class="actions">

           <form method="post"
                 action="<%= request.getContextPath() %>/logout">

    <input type="hidden"
           name="csrfToken"
           value="<%= request.getAttribute("csrfToken") %>">


               <button type="submit">
                   Logout
               </button>

           </form>

        </div>

    </div>

</div>

</body>
</html>