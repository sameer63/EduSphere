<%@ page import="com.edusphere.model.Course" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%

    Course course =
            (Course) request.getAttribute("course");

    Boolean enrolled =
            (Boolean) request.getAttribute("enrolled");
%>

<!DOCTYPE html>

<html>

<head>

    <title>
        <%= course.getTitle() %>
    </title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        .container {
            max-width: 800px;
            margin: auto;
        }

        .card {
            border: 1px solid #ddd;
            border-radius: 8px;
            padding: 30px;
        }

        .title {
            font-size: 32px;
            font-weight: bold;
            margin-bottom: 20px;
        }

        .description {
            line-height: 1.7;
            margin-bottom: 25px;
        }

        .info {
            margin: 12px 0;
        }

        .actions {
            margin-top: 30px;
        }

        button {
            padding: 10px 18px;
            cursor: pointer;
        }

        .enrolled {
            padding: 12px;
            border: 1px solid #999;
            margin-bottom: 20px;
        }

        a {
            margin-right: 15px;
        }

    </style>

</head>

<body>

<div class="container">

    <div class="card">

        <div class="title">
            <%= course.getTitle() %>
        </div>


        <div class="description">

            <%
                if (course.getDescription() != null
                        && !course.getDescription().isBlank()) {
            %>

                <%= course.getDescription() %>

            <%
                } else {
            %>

                No description available.

            <%
                }
            %>

        </div>


        <div class="info">

            <strong>Price:</strong>

            ₹<%= course.getPrice() %>

        </div>


        <div class="info">

            <strong>Status:</strong>

            <%= course.getStatus() %>

        </div>


       <%
           if (Boolean.TRUE.equals(enrolled)) {
       %>

           <div class="enrolled">

               <strong>You are already enrolled in this course.</strong>

           </div>

           <div class="actions">

               <a href="<%= request.getContextPath() %>/student/courses/quizzes?courseId=<%= course.getId() %>">
                   View Quizzes
               </a>

           </div>

       <%
           } else {
       %>

            <div class="actions">

                <form method="post"
                      action="<%= request.getContextPath() %>/student/courses/enrol">

                      <input type="hidden"
                             name="csrfToken"
                             value="<%= request.getAttribute("csrfToken") %>">

                    <input
                            type="hidden"
                            name="courseId"
                            value="<%= course.getId() %>"
                    >

                    <button type="submit">
                        Enrol Now
                    </button>

                </form>
                <br/>
                <form method="post"
                      action="<%= request.getContextPath() %>/student/payments/create">

                      <input type="hidden"
                             name="csrfToken"
                             value="<%= request.getAttribute("csrfToken") %>">

                    <input type="hidden"
                           name="courseId"
                           value="<%= course.getId() %>">

                    <button type="submit">
                        Pay <%= course.getPrice() %> <%= "INR" %>
                    </button>

                </form>

            </div>

        <%
            }
        %>


        <div class="actions">

            <a href="<%= request.getContextPath() %>/student/courses">
                ← Browse Courses
            </a>

            <a href="<%= request.getContextPath() %>/student/dashboard">
                Dashboard
            </a>

        </div>

    </div>

</div>

</body>

</html>