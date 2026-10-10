<%@ page import="com.edusphere.model.Course" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%
    Course course =
            (Course) request.getAttribute("course");
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
            padding: 25px;
        }

        .title {
            font-size: 30px;
            font-weight: bold;
            margin-bottom: 20px;
        }

        .description {
            line-height: 1.6;
            margin-bottom: 25px;
        }

        .info {
            margin-bottom: 12px;
        }

        .actions {
            margin-top: 30px;
        }

        .actions a {
            display: inline-block;
            padding: 10px 16px;
            margin-right: 10px;
            border: 1px solid #333;
            border-radius: 5px;
            text-decoration: none;
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
                if (course.getDescription() != null &&
                        !course.getDescription().isBlank()) {
            %>

                <%= course.getDescription() %>

            <%
                } else {
            %>

                No description provided.

            <%
                }
            %>

        </div>


        <div class="info">

            <strong>Course ID:</strong>

            <%= course.getId() %>

        </div>


        <div class="info">

            <strong>Price:</strong>

            ₹<%= course.getPrice() %>

        </div>


        <div class="info">

            <strong>Status:</strong>

            <%= course.getStatus() %>

        </div>


        <div class="info">

            <strong>Teacher ID:</strong>

            <%= course.getTeacherId() %>

        </div>


        <div class="info">

            <strong>Category ID:</strong>

            <%= course.getCategoryId() %>

        </div>


        <%
            if (course.getCreatedAt() != null) {
        %>

            <div class="info">

                <strong>Created:</strong>

                <%= course.getCreatedAt() %>

            </div>

        <%
            }
        %>


        <%
            if (course.getUpdatedAt() != null) {
        %>

            <div class="info">

                <strong>Last Updated:</strong>

                <%= course.getUpdatedAt() %>

            </div>

        <%
            }
        %>
       <div class="actions">

           <a href="<%= request.getContextPath() %>/teacher/courses/edit?id=<%= course.getId() %>">
               Edit Course
           </a>

           <a href="<%= request.getContextPath() %>/teacher/courses/lessons?courseId=<%= course.getId() %>">
               Manage Lessons
           </a>

           <p>
               <a href="<%= request.getContextPath() %>/teacher/courses/quizzes?courseId=<%= course.getId() %>">
                   Manage Quizzes
               </a>
           </p>

           <a href="<%= request.getContextPath() %>/teacher/courses/assignments?courseId=<%= course.getId() %>">
               Manage Assignments
           </a>

           <%
               if ("DRAFT".equals(course.getStatus())) {
           %>

               <form method="post"
                     action="<%= request.getContextPath() %>/teacher/courses/status"
                     style="display:inline;">

                     <input type="hidden"
                            name="csrfToken"
                            value="<%= request.getAttribute("csrfToken") %>">

                   <input
                           type="hidden"
                           name="id"
                           value="<%= course.getId() %>"
                   >

                   <input
                           type="hidden"
                           name="status"
                           value="PUBLISHED"
                   >

                   <button type="submit">
                       Publish Course
                   </button>

               </form>

           <%
               }
           %>


           <%
               if ("PUBLISHED".equals(course.getStatus())) {
           %>

               <form method="post"
                     action="<%= request.getContextPath() %>/teacher/courses/status"
                     style="display:inline;">

                     <input type="hidden"
                            name="csrfToken"
                            value="<%= request.getAttribute("csrfToken") %>">

                   <input
                           type="hidden"
                           name="id"
                           value="<%= course.getId() %>"
                   >

                   <input
                           type="hidden"
                           name="status"
                           value="ARCHIVED"
                   >

                   <button type="submit">
                       Archive Course
                   </button>

               </form>

           <%
               }
           %>


           <a href="<%= request.getContextPath() %>/teacher/courses">
               Back to My Courses
           </a>

       </div>

    </div>

</div>

</body>

</html>