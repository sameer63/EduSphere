package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Quiz;
import com.edusphere.model.QuizAttempt;
import com.edusphere.model.QuizAttemptAnswer;
import com.edusphere.model.QuizOption;
import com.edusphere.model.QuizQuestion;
import com.edusphere.model.Student;
import com.edusphere.service.CourseService;
import com.edusphere.service.QuizAttemptService;
import com.edusphere.service.QuizOptionService;
import com.edusphere.service.QuizQuestionService;
import com.edusphere.service.QuizService;
import com.edusphere.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/student/courses/quizzes/attempt")
public class StudentQuizAttemptServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final QuizAttemptService quizAttemptService =
            new QuizAttemptService();

    private final QuizService quizService =
            new QuizService();

    private final CourseService courseService =
            new CourseService();

    private final QuizQuestionService quizQuestionService =
            new QuizQuestionService();

    private final QuizOptionService quizOptionService =
            new QuizOptionService();

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            HttpSession session =
                    request.getSession(false);

            if (session == null) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                return;
            }

            Long userId =
                    (Long) session.getAttribute("userId");

            if (userId == null) {

                response.sendError(
                        HttpServletResponse.SC_UNAUTHORIZED
                );

                return;
            }

            /*
             * Find student profile.
             */
            Student student =
                    studentService.findByUserId(userId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Student profile not found."
                                    )
                            );

            /*
             * Get attempt ID.
             */
            String attemptIdParam =
                    request.getParameter("id");

            if (attemptIdParam == null ||
                    attemptIdParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Attempt ID is required."
                );

                return;
            }

            Long attemptId;

            try {

                attemptId =
                        Long.parseLong(attemptIdParam);

            } catch (NumberFormatException e) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Invalid attempt ID."
                );

                return;
            }

            /*
             * Load attempt.
             */
            QuizAttempt attempt =
                    quizAttemptService.findById(attemptId)
                            .orElseThrow(() ->
                                    new IllegalArgumentException(
                                            "Quiz attempt not found."
                                    )
                            );

            /*
             * Security check:
             * the attempt must belong to the
             * logged-in student.
             */
            if (!attempt.getStudentId()
                    .equals(student.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to view this attempt."
                );

                return;
            }

            /*
             * Load quiz.
             */
            Quiz quiz =
                    quizService.findById(attempt.getQuizId());
            if(quiz==null){
                throw new IllegalArgumentException("Quiz not found");
            }

            /*
             * Load course.
             */
            Course course =
                    courseService.findById(quiz.getCourseId());

            /*
             * Load all questions.
             */
            List<QuizQuestion> questions =
                    quizQuestionService.findByQuiz(
                            quiz.getId()
                    );

            /*
             * Map:
             *
             * question ID
             *      ↓
             * list of options
             */
            Map<Long, List<QuizOption>> optionsByQuestion =
                    new HashMap<>();

            for (QuizQuestion question : questions) {

                List<QuizOption> options =
                        quizOptionService.findByQuestion(
                                question.getId()
                        );

                optionsByQuestion.put(
                        question.getId(),
                        options
                );
            }

            /*
             * Load answers already saved
             * for this attempt.
             */
            List<QuizAttemptAnswer> answers =
                    quizAttemptService.findAnswers(
                            student.getId(),
                            attemptId
                    );

            /*
             * Map:
             *
             * question ID → selected option ID
             */
            Map<Long, Long> selectedOptions =
                    new HashMap<>();

            for (QuizAttemptAnswer answer : answers) {

                selectedOptions.put(
                        answer.getQuestionId(),
                        answer.getOptionId()
                );
            }

            request.setAttribute(
                    "student",
                    student
            );

            request.setAttribute(
                    "attempt",
                    attempt
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

            request.setAttribute(
                    "optionsByQuestion",
                    optionsByQuestion
            );

            request.setAttribute(
                    "selectedOptions",
                    selectedOptions
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/quiz-attempt.jsp"
            ).forward(request, response);

        } catch (SecurityException e) {

            response.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    e.getMessage()
            );

        } catch (Exception e) {

            throw new ServletException(
                    "Unable to load quiz attempt.",
                    e
            );
        }
    }
}