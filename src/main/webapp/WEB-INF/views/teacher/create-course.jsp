<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Category" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html>

<head>

    <title>Create Course</title>

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

    <h1>Create Course</h1>

    <%
        String error = (String) request.getAttribute("error");

        if (error != null) {
    %>

        <div class="error">
            <%= error %>
        </div>

    <%
        }
    %>

    <form method="post"
          action="<%= request.getContextPath() %>/teacher/courses/create">
          <input type="hidden"
                 name="csrfToken"
                 value="<%= request.getAttribute("csrfToken") %>">

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
                    value="<%= request.getAttribute("title") != null
                            ? request.getAttribute("title")
                            : "" %>"
            >

        </div>


        <div class="form-group">

            <label for="description">
                Description
            </label>

            <textarea
                    id="description"
                    name="description"
            ><%= request.getAttribute("description") != null
                    ? request.getAttribute("description")
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

                <option value="">
                    -- Select Category --
                </option>

                <%
                    List<Category> categories =
                            (List<Category>)
                                    request.getAttribute("categories");

                    if (categories != null) {

                        for (Category category : categories) {
                %>

                    <option value="<%= category.getId() %>">
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
                    value="<%= request.getAttribute("price") != null
                            ? request.getAttribute("price")
                            : "0" %>"
            >

        </div>


        <div class="form-group">

            <label for="status">
                Status
            </label>

            <select
                    id="status"
                    name="status">

                <option value="DRAFT">
                    Draft
                </option>

                <option value="PUBLISHED">
                    Published
                </option>

                <option value="ARCHIVED">
                    Archived
                </option>

            </select>

        </div>


        <button type="submit">
            Create Course
        </button>

    </form>

    <br>

    <a href="<%= request.getContextPath() %>/teacher/dashboard">
        ← Back to Dashboard
    </a>

</div>

</body>

</html>