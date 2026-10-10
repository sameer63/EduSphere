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
import java.util.List;

@WebServlet("/teacher/courses/quizzes/questions")
public class TeacherQuizQuestionsServlet extends HttpServlet {

    private final QuizService quizService =
            new QuizService();

    private final QuizQuestionService quizQuestionService =
            new QuizQuestionService();

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

            List<QuizQuestion> questions =
                    quizQuestionService.findByQuiz(
                            quizId
                    );

            request.setAttribute(
                    "quiz",
                    quiz
            );

            request.setAttribute(
                    "course",
                    course
            );

            request.setAttribute(
                    "questions",
                    questions
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/questions.jsp"
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
                    "Unable to load quiz questions.",
                    e
            );
        }
    }
}