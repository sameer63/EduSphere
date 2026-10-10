package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
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

@WebServlet("/teacher/courses/quizzes/questions/create")
public class CreateQuizQuestionServlet extends HttpServlet {

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

            String quizIdParam =
                    request.getParameter("quizId");

            if (quizIdParam == null ||
                    quizIdParam.isBlank()) {

                throw new IllegalArgumentException(
                        "Quiz ID is required."
                );
            }

            Long quizId =
                    Long.parseLong(quizIdParam);

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {
                throw new IllegalArgumentException(
                        "Teacher profile not found."
                );
            }

            Quiz quiz =
                    quizService.findById(quizId);

            if (quiz == null) {
                throw new IllegalArgumentException(
                        "Quiz not found."
                );
            }

            Course course =
                    courseService.findById(
                            quiz.getCourseId()
                    );

            if (course == null) {
                throw new IllegalArgumentException(
                        "Course not found."
                );
            }

            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You do not own this quiz."
                );

                return;
            }

            request.setAttribute(
                    "quiz",
                    quiz
            );

            request.setAttribute(
                    "course",
                    course
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/create-question.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid quiz ID."
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/error/error.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to load create question page.",
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

        String quizIdParam =
                request.getParameter("quizId");

        String questionText =
                request.getParameter("questionText");

        String marksParam =
                request.getParameter("marks");

        String positionParam =
                request.getParameter("position");

        try {

            Long userId =
                    (Long) session.getAttribute("userId");

            if (quizIdParam == null ||
                    quizIdParam.isBlank()) {

                throw new IllegalArgumentException(
                        "Quiz ID is required."
                );
            }

            Long quizId =
                    Long.parseLong(quizIdParam);

            Integer marks =
                    Integer.parseInt(marksParam);

            Integer position =
                    Integer.parseInt(positionParam);

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {
                throw new IllegalArgumentException(
                        "Teacher profile not found."
                );
            }

            QuizQuestionService service =
                    quizQuestionService;

            service.createQuestion(
                    teacher.getId(),
                    quizId,
                    questionText,
                    marks,
                    position
            );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/quizzes/questions?quizId="
                            + quizId
            );

        } catch (NumberFormatException e) {

            loadFormAfterError(
                    request,
                    response,
                    quizIdParam,
                    questionText,
                    marksParam,
                    positionParam,
                    "Quiz ID, marks and position must be valid numbers."
            );

        } catch (IllegalArgumentException e) {

            loadFormAfterError(
                    request,
                    response,
                    quizIdParam,
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
                    "Unable to create quiz question.",
                    e
            );
        }
    }

    private void loadFormAfterError(
            HttpServletRequest request,
            HttpServletResponse response,
            String quizIdParam,
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

            Long quizId =
                    Long.parseLong(quizIdParam);

            Quiz quiz =
                    quizService.findById(quizId);

            if (quiz != null) {

                request.setAttribute(
                        "quiz",
                        quiz
                );

                Course course =
                        courseService.findById(
                                quiz.getCourseId()
                        );

                request.setAttribute(
                        "course",
                        course
                );
            }

        } catch (Exception ignored) {
        }

        request.getRequestDispatcher(
                "/WEB-INF/views/teacher/create-question.jsp"
        ).forward(request, response);
    }
}