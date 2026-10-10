package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Teacher;
import com.edusphere.service.CourseService;
import com.edusphere.service.QuizQuestionService;
import com.edusphere.service.QuizService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/teacher/courses/quizzes/questions/edit")
public class EditQuizQuestionServlet extends HttpServlet {

    private final QuizQuestionService quizQuestionService =
            new QuizQuestionService();

    private final QuizService quizService =
            new QuizService();

    private final TeacherService teacherService =
            new TeacherService();

    private final CourseService courseService =
            new CourseService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {

            Long userId =
                    (Long) session.getAttribute("userId");

            Long questionId =
                    Long.parseLong(
                            request.getParameter("id")
                    );

            Teacher teacher =
                    teacherService.findByUserId(userId);

            QuizQuestion question =
                    quizQuestionService.findById(
                            questionId
                    );

            if (question == null) {
                throw new IllegalArgumentException(
                        "Question not found."
                );
            }

            Quiz quiz =
                    quizService.findById(
                            question.getQuizId()
                    );

            if (quiz == null) {
                throw new IllegalArgumentException(
                        "Quiz not found."
                );
            }

            Course course =
                    courseService.findById(
                            quiz.getCourseId()
                    );

            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You do not own this question."
                );

                return;
            }

            request.setAttribute(
                    "question",
                    question
            );

            request.setAttribute(
                    "quiz",
                    quiz
            );

            request.setAttribute(
                    "course",
                    course
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/edit-question.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid question ID."
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to load edit question page.",
                    e
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session =
                request.getSession(false);

        if (session == null ||
                session.getAttribute("userId") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        String idParam =
                request.getParameter("id");

        String questionText =
                request.getParameter("questionText");

        String marksParam =
                request.getParameter("marks");

        String positionParam =
                request.getParameter("position");

        try {

            Long userId =
                    (Long) session.getAttribute("userId");

            Long questionId =
                    Long.parseLong(idParam);

            Integer marks =
                    Integer.parseInt(marksParam);

            Integer position =
                    Integer.parseInt(positionParam);

            Teacher teacher =
                    teacherService.findByUserId(userId);

            QuizQuestion updated =
                    quizQuestionService.updateQuestion(
                            teacher.getId(),
                            questionId,
                            questionText,
                            marks,
                            position
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/quizzes/questions/view?id="
                            + updated.getId()
            );

        } catch (NumberFormatException e) {

            loadFormWithError(
                    request,
                    response,
                    idParam,
                    questionText,
                    marksParam,
                    positionParam,
                    "Question ID, marks and position must be valid numbers."
            );

        } catch (IllegalArgumentException e) {

            loadFormWithError(
                    request,
                    response,
                    idParam,
                    questionText,
                    marksParam,
                    positionParam,
                    e.getMessage()
            );

        } catch (SecurityException e) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to update question.",
                    e
            );
        }
    }

    private void loadFormWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            String idParam,
            String questionText,
            String marks,
            String position,
            String error)
            throws ServletException, IOException {

        request.setAttribute(
                "error",
                error
        );

        request.setAttribute(
                "questionText",
                questionText
        );

        request.setAttribute(
                "marks",
                marks
        );

        request.setAttribute(
                "position",
                position
        );

        try {

            Long questionId =
                    Long.parseLong(idParam);

            QuizQuestion question =
                    quizQuestionService.findById(
                            questionId
                    );

            if (question != null) {

                request.setAttribute(
                        "question",
                        question
                );

                Quiz quiz =
                        quizService.findById(
                                question.getQuizId()
                        );

                request.setAttribute(
                        "quiz",
                        quiz
                );

                if (quiz != null) {

                    Course course =
                            courseService.findById(
                                    quiz.getCourseId()
                            );

                    request.setAttribute(
                            "course",
                            course
                    );
                }
            }

        } catch (Exception ignored) {
        }

        request.getRequestDispatcher(
                "/WEB-INF/views/teacher/edit-question.jsp"
        ).forward(request, response);
    }
}