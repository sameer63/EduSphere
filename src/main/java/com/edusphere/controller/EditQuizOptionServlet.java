package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizOption;
import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Teacher;
import com.edusphere.service.CourseService;
import com.edusphere.service.QuizOptionService;
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

@WebServlet("/teacher/courses/quizzes/questions/options/edit")
public class EditQuizOptionServlet extends HttpServlet {

    private final QuizOptionService quizOptionService =
            new QuizOptionService();

    private final QuizQuestionService quizQuestionService =
            new QuizQuestionService();

    private final QuizService quizService =
            new QuizService();

    private final CourseService courseService =
            new CourseService();

    private final TeacherService teacherService =
            new TeacherService();

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

            Long optionId =
                    Long.parseLong(
                            request.getParameter("id")
                    );

            Teacher teacher =
                    teacherService.findByUserId(userId);

            if (teacher == null) {
                throw new IllegalArgumentException(
                        "Teacher profile not found."
                );
            }

            QuizOption option =
                    quizOptionService.findById(
                            optionId
                    );

            if (option == null) {
                throw new IllegalArgumentException(
                        "Option not found."
                );
            }

            QuizQuestion question =
                    quizQuestionService.findById(
                            option.getQuestionId()
                    );

            Quiz quiz =
                    quizService.findById(
                            question.getQuizId()
                    );

            Course course =
                    courseService.findById(
                            quiz.getCourseId()
                    );

            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You do not own this option."
                );

                return;
            }

            request.setAttribute(
                    "option",
                    option
            );

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
                    "/WEB-INF/views/teacher/edit-option.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid option ID."
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to load edit option page.",
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

        String optionText =
                request.getParameter("optionText");

        String correctParam =
                request.getParameter("correct");

        try {

            Long userId =
                    (Long) session.getAttribute("userId");

            Long optionId =
                    Long.parseLong(idParam);

            boolean correct =
                    "true".equalsIgnoreCase(
                            correctParam
                    );

            Teacher teacher =
                    teacherService.findByUserId(userId);

            QuizOption updated =
                    quizOptionService.updateOption(
                            teacher.getId(),
                            optionId,
                            optionText,
                            correct
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/quizzes/questions/options?questionId="
                            + updated.getQuestionId()
            );

        } catch (NumberFormatException e) {

            loadFormWithError(
                    request,
                    response,
                    idParam,
                    optionText,
                    correctParam,
                    "Invalid option ID."
            );

        } catch (IllegalArgumentException e) {

            loadFormWithError(
                    request,
                    response,
                    idParam,
                    optionText,
                    correctParam,
                    e.getMessage()
            );

        } catch (SecurityException e) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to update option.",
                    e
            );
        }
    }

    private void loadFormWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            String idParam,
            String optionText,
            String correctParam,
            String error)
            throws ServletException, IOException {

        request.setAttribute(
                "error",
                error
        );

        request.setAttribute(
                "optionText",
                optionText
        );

        request.setAttribute(
                "correct",
                correctParam
        );

        try {

            Long optionId =
                    Long.parseLong(idParam);

            QuizOption option =
                    quizOptionService.findById(
                            optionId
                    );

            if (option != null) {

                request.setAttribute(
                        "option",
                        option
                );

                QuizQuestion question =
                        quizQuestionService.findById(
                                option.getQuestionId()
                        );

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
                "/WEB-INF/views/teacher/edit-option.jsp"
        ).forward(request, response);
    }
}