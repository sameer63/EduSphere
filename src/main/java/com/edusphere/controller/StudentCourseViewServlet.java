package com.edusphere.controller;

import com.edusphere.model.Course;
import com.edusphere.model.Student;
import com.edusphere.service.CourseService;
import com.edusphere.service.EnrolmentService;

import com.edusphere.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/student/courses/view")
public class StudentCourseViewServlet extends HttpServlet {

    private CourseService courseService;
    private EnrolmentService enrolmentService;

    @Override
    public void init() {

        courseService = new CourseService();
        enrolmentService = new EnrolmentService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            HttpSession session =
                    request.getSession(false);

            if (session == null) {

                response.sendRedirect(
                        request.getContextPath() + "/login"
                );

                return;
            }

            Long userId =
                    (Long) session.getAttribute("userId");

            if (userId == null) {

                response.sendRedirect(
                        request.getContextPath() + "/login"
                );

                return;
            }

            String idParameter =
                    request.getParameter("id");

            if (idParameter == null ||
                    idParameter.isBlank()) {

                response.sendError(
                        HttpServletResponse.SC_BAD_REQUEST,
                        "Course ID is required."
                );

                return;
            }

            Long courseId =
                    Long.parseLong(idParameter);

            Course course =
                    courseService.findById(courseId);

            /*
             * Students should only see published
             * courses through the public catalogue.
             */
            if (!"PUBLISHED".equals(course.getStatus())) {

                response.sendError(
                        HttpServletResponse.SC_NOT_FOUND,
                        "Course not found."
                );

                return;
            }

            /*
             * Determine whether this student is
             * already enrolled.
             *
             * We have userId in the session, but the
             * enrolment table uses studentId.
             *
             * StudentService resolves it.
             */
            StudentService studentService = new StudentService();

            Optional<Student> optionalStudent =
                    studentService.findByUserId(userId);
            Student student=optionalStudent.get();

            boolean enrolled =
                    enrolmentService.isStudentEnrolled(
                            student.getId(),
                            courseId
                    );

            request.setAttribute(
                    "course",
                    course
            );

            request.setAttribute(
                    "enrolled",
                    enrolled
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/student/course-details.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendError(
                    HttpServletResponse.SC_BAD_REQUEST,
                    "Invalid course ID."
            );

        } catch (IllegalArgumentException e) {

            response.sendError(
                    HttpServletResponse.SC_NOT_FOUND,
                    e.getMessage()
            );

        } catch (Exception e) {

            getServletContext().log(
                    "Error loading student course details",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load course."
            );
        }
    }
}