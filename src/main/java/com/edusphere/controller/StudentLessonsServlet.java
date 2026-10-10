package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Enrolment;
import com.edusphere.model.Student;
import com.edusphere.model.Lesson;
import com.edusphere.service.CourseService;
import com.edusphere.service.EnrolmentService;
import com.edusphere.service.LessonService;
import com.edusphere.service.StudentService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/student/courses/lessons")
public class StudentLessonsServlet extends HttpServlet {

    private final StudentService studentService =
            new StudentService();

    private final CourseService courseService =
            new CourseService();

    private final EnrolmentService enrolmentService =
            new EnrolmentService();

    private final LessonService lessonService =
            new LessonService();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        try {

            // 1. Get existing session
            HttpSession session =
                    request.getSession(false);

            if (session == null ||
                    session.getAttribute("userId") == null) {

                response.sendRedirect(
                        request.getContextPath() + "/login"
                );

                return;
            }

            // 2. Get logged-in user's ID
            Long userId =
                    (Long) session.getAttribute("userId");

            // 3. Get course ID
            String courseIdParam =
                    request.getParameter("courseId");

            if (courseIdParam == null ||
                    courseIdParam.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Course ID is required."
                );

                return;
            }

            Long courseId =
                    Long.parseLong(courseIdParam);

            // 4. Find student using logged-in user
            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);
            if(optionalStudent.isEmpty()){
                throw new IllegalArgumentException("Student not found");
            }
            Student student=optionalStudent.get();

            if (student == null) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "Student profile not found."
                );

                return;
            }

            // 5. Find course
            Course course =
                    courseService.findById(courseId);

            if (course == null) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );

                return;
            }

            // 6. Verify enrolment
            boolean enrolled =
                    enrolmentService.isStudentEnrolled(
                            student.getId(),
                            courseId
                    );

            if (!enrolled) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You must be enrolled in this course to access its lessons."
                );

                return;
            }

            // 7. Get lessons
            List<Lesson> lessons =
                    lessonService.findByCourse(courseId);

            // 8. Send data to JSP
            request.setAttribute("course", course);
            request.setAttribute("lessons", lessons);

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/lessons.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid course ID."
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Something went wrong while loading the lessons."
            );
        }
    }
}