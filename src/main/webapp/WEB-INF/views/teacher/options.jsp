<%@ page contentType="text/html;charset=UTF-8" %>

<%@ page import="java.util.List" %>
<%@ page import="com.edusphere.model.QuizOption" %>
<%@ page import="com.edusphere.model.QuizQuestion" %>
<%@ page import="com.edusphere.model.Quiz" %>
<%@ page import="com.edusphere.model.Course" %>

<%
    QuizOption selectedOption =
            (QuizOption) request.getAttribute("selectedOption");

    QuizQuestion question =
            (QuizQuestion) request.getAttribute("question");

    Quiz quiz =
            (Quiz) request.getAttribute("quiz");

    Course course =
            (Course) request.getAttribute("course");

    List<QuizOption> options =
            (List<QuizOption>)
                    request.getAttribute("options");
%>

<!DOCTYPE html>
<html>

<head>

    <meta charset="UTF-8">

    <title>
        Manage Options
    </title>

</head>

<body>

<h1>
    Manage Question Options
</h1>

<p>
    Course:
    <strong>
        <%= course.getTitle() %>
    </strong>
</p>

<p>
    Quiz:
    <strong>
        <%= quiz.getTitle() %>
    </strong>
</p>

<hr>

<h3>
    Question <%= question.getPosition() %>
</h3>

<p>
    <%= question.getQuestionText() %>
</p>

<p>
    Marks:
    <%= question.getMarks() %>
</p>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/options/create?questionId=<%= question.getId() %>">
        Add New Option
    </a>

</p>

<hr>

<% if (options == null || options.isEmpty()) { %>

    <p>
        No options have been added yet.
    </p>

<% } else { %>

    <table border="1" cellpadding="8">

        <thead>

        <tr>

            <th>Option</th>
            <th>Correct?</th>
            <th>Actions</th>

        </tr>

        </thead>

        <tbody>

        <% for (QuizOption option : options) { %>

            <tr>

                <td>
                    <%= option.getOptionText() %>
                </td>

                <td>

                    <% if (option.isCorrect()) { %>

                        <strong>
                            ✓ Correct
                        </strong>

                    <% } else { %>

                        No

                    <% } %>

                </td>

                <td>

                    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/options/edit?id=<%= option.getId() %>">
                        Edit
                    </a>

                    |

                    <form method="post"
                          action="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/options/delete"
                          style="display:inline;"
                          onsubmit="return confirm('Are you sure you want to delete this option?');">

                          <input type="hidden"
                                 name="csrfToken"
                                 value="<%= request.getAttribute("csrfToken") %>">

                        <input type="hidden"
                               name="id"
                               value="<%= option.getId() %>">

                        <button type="submit">
                            Delete
                        </button>

                    </form>

                    <% if (!option.isCorrect()) { %>

                        |

                        <form method="post"
                              action="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/options/correct"
                              style="display:inline;">

                              <input type="hidden"
                                     name="csrfToken"
                                     value="<%= request.getAttribute("csrfToken") %>">

                            <input type="hidden"
                                   name="id"
                                   value="<%= option.getId() %>">

                            <button type="submit">
                                Mark Correct
                            </button>

                        </form>

                    <% } %>

                </td>

            </tr>

        <% } %>

        </tbody>

    </table>

<% } %>

<hr>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions/view?id=<%= question.getId() %>">
        Back to Question
    </a>

</p>

<p>

    <a href="<%= request.getContextPath() %>/teacher/courses/quizzes/questions?quizId=<%= quiz.getId() %>">
        Back to Questions
    </a>

</p>

</body>

</html>