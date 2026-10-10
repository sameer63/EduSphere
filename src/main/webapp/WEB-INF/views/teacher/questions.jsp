<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>
<%@ page import="com.edusphere.model.QuizQuestion" %>

<%
    Quiz quiz =
            (Quiz) request.getAttribute("quiz");

    Course course =
            (Course) request.getAttribute("course");

    List<QuizQuestion> questions =
            (List<QuizQuestion>)
                    request.getAttribute("questions");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Manage Questions
    </title>

</head>

<body>

<h1>
    Manage Quiz Questions
</h1>

<h2>
    <%= quiz.getTitle() %>
</h2>

<p>
    Course:
    <strong>
        <%= course.getTitle() %>
    </strong>
</p>

<p>
    Maximum Attempts:
    <%= quiz.getMaxAttempts() %>
</p>

<hr>

<p>
    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/create?quizId=<%= quiz.getId() %>">
        Add New Question
    </a>
</p>

<hr>

<% if (questions == null || questions.isEmpty()) { %>

    <p>
        No questions have been added to this quiz yet.
    </p>

<% } else { %>

    <table border="1" cellpadding="8">

        <thead>

        <tr>
            <th>Position</th>
            <th>Question</th>
            <th>Marks</th>
            <th>Actions</th>
        </tr>

        </thead>

        <tbody>

        <% for (QuizQuestion question : questions) { %>

            <tr>

                <td>
                    <%= question.getPosition() %>
                </td>

                <td>
                    <%= question.getQuestionText() %>
                </td>

                <td>
                    <%= question.getMarks() %>
                </td>

                <td>

                    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/view?id=<%= question.getId() %>">
                        View
                    </a>

                    |

                    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/edit?id=<%= question.getId() %>">
                        Edit
                    </a>

                    |

                    <form method="post"
                          action="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/delete"
                          style="display:inline;"
                          onsubmit="return confirm('Are you sure you want to delete this question?');">

                          <input type="hidden"
                                 name="csrfToken"
                                 value="<%= request.getAttribute("csrfToken") %>">

                        <input type="hidden"
                               name="id"
                               value="<%= question.getId() %>">

                        <button type="submit">
                            Delete
                        </button>

                    </form>

                </td>

            </tr>

        <% } %>

        </tbody>

    </table>

<% } %>

<hr>

<p>
    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/view?id=<%= quiz.getId() %>">
        Back to Quiz
    </a>
</p>

<p>
    <a href="<%= request.getContextPath() %>/teacher/courses/view?id=<%= course.getId() %>">
        Back to Course
    </a>
</p>

</body>

</html>