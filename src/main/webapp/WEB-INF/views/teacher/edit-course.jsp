<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.Category" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<%
    Course course =
            (Course) request.getAttribute("course");

    List<Category> categories =
            (List<Category>) request.getAttribute("categories");

    String error =
            (String) request.getAttribute("error");
%>

<!DOCTYPE html>

<html>

<head>

    <title>Edit Course</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 40px;
        }

        .container {
            max-width: 700px;
            margin: auto;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: bold;
        }

        input,
        textarea,
        select {
            width: 100%;
            padding: 10px;
            box-sizing: border-box;
        }

        textarea {
            min-height: 150px;
        }

        button {
            padding: 10px 20px;
            cursor: pointer;
        }

        .error {
            background: #ffe5e5;
            padding: 12px;
            margin-bottom: 20px;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>Edit Course</h1>

    <%
        if (error != null) {
    %>

        <div class="error">
            <%= error %>
        </div>

    <%
        }
    %>


    <form method="post"
          action="<%= request.getContextPath() %>/teacher/courses/edit">

      <input type="hidden"
             name="csrfToken"
             value="<%= request.getAttribute("csrfToken") %>">
        <input
                type="hidden"
                name="id"
                value="<%= course.getId() %>"
        >


        <div class="form-group">

            <label for="title">
                Course Title
            </label>

            <input
                    type="text"
                    id="title"
                    name="title"
                    maxlength="200"
                    required
                    value="<%= course.getTitle() %>"
            >

        </div>


        <div class="form-group">

            <label for="description">
                Description
            </label>

            <textarea
                    id="description"
                    name="description"
            ><%= course.getDescription() != null
                    ? course.getDescription()
                    : "" %></textarea>

        </div>


        <div class="form-group">

            <label for="categoryId">
                Category
            </label>

            <select
                    id="categoryId"
                    name="categoryId"
                    required>

                <%
                    if (categories != null) {

                        for (Category category : categories) {

                            boolean selected =
                                    category.getId()
                                            .equals(course.getCategoryId());
                %>

                    <option
                            value="<%= category.getId() %>"
                            <%= selected ? "selected" : "" %>
                    >
                        <%= category.getName() %>
                    </option>

                <%
                        }
                    }
                %>

            </select>

        </div>


        <div class="form-group">

            <label for="price">
                Price (INR)
            </label>

            <input
                    type="number"
                    id="price"
                    name="price"
                    min="0"
                    step="0.01"
                    required
                    value="<%= course.getPrice() %>"
            >

        </div>


        <div class="form-group">

            <label for="status">
                Status
            </label>

            <select
                    id="status"
                    name="status">

                <option
                        value="DRAFT"
                        <%= "DRAFT".equals(course.getStatus())
                                ? "selected"
                                : "" %>
                >
                    Draft
                </option>

                <option
                        value="PUBLISHED"
                        <%= "PUBLISHED".equals(course.getStatus())
                                ? "selected"
                                : "" %>
                >
                    Published
                </option>

                <option
                        value="ARCHIVED"
                        <%= "ARCHIVED".equals(course.getStatus())
                                ? "selected"
                                : "" %>
                >
                    Archived
                </option>

            </select>

        </div>


        <button type="submit">
            Update Course
        </button>

    </form>

    <br>

    <a href="<%= request.getContextPath() %>/teacher/courses/view?id=<%= course.getId() %>">
        ← Back to Course
    </a>

</div>

</body>

</html>