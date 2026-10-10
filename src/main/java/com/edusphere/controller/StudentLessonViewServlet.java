package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Enrolment;
import com.edusphere.model.Lesson;
import com.edusphere.model.Student;
import com.edusphere.service.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@WebServlet("/student/courses/lessons/view")
public class StudentLessonViewServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final LessonService lessonService =
            new LessonService();

    private final CourseService courseService =
            new CourseService();

    private final EnrolmentService enrolmentService =
            new EnrolmentService();
    private final LearningActivityService learningActivityService =
            new LearningActivityService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {

            // Get existing session
            HttpSession session =
                    request.getSession(false);

            if (session == null ||
                    session.getAttribute("userId") == null) {

                response.sendRedirect(
                        request.getContextPath() + "/login"
                );

                return;
            }

            // Logged-in user
            Long userId =
                    (Long) session.getAttribute("userId");

            // Lesson ID
            String idParam =
                    request.getParameter("id");

            if (idParam == null ||
                    idParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Lesson ID is required."
                );

                return;
            }

            Long lessonId =
                    Long.parseLong(idParam);

            // Find student
            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);
            Student student=optionalStudent.get();

            if (student == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );

                return;
            }

            // Find lesson
            Lesson lesson =
                    lessonService.findById(lessonId);

            if (lesson == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Lesson not found."
                );

                return;
            }

            // Find course
            Course course =
                    courseService.findById(
                            lesson.getCourseId()
                    );

            if (course == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );

                return;
            }

            // Verify enrolment
            boolean enrolled =
                    enrolmentService.isStudentEnrolled(
                            student.getId(),
                            course.getId()
                    );

            if (!enrolled) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You must be enrolled in this course to access this lesson."
                );

                return;
            }

            try {
                learningActivityService.recordActivity(
                        userId,
                        student.getId(),
                        "LESSON_VIEWED",
                        lesson.getCourseId(),
                        lesson.getId(),
                        Map.of("source", "student-portal")
                );
            } catch (Exception e) {
                getServletContext().log(
                        "Unable to record lesson-view activity.",
                        e
                );
            }

            // Send lesson and course to JSP
            request.setAttribute(
                    "lesson",
                    lesson
            );

            request.setAttribute(
                    "course",
                    course
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/lesson-view.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid lesson ID."
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong while loading the lesson."
            );
        }
    }
}