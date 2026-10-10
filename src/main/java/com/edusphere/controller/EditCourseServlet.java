package com.edusphere.controller;

import com.edusphere.model.Category;
import com.edusphere.model.Course;
import com.edusphere.model.Teacher;
import com.edusphere.service.CategoryService;
import com.edusphere.service.CourseService;
import com.edusphere.service.TeacherService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/teacher/courses/edit")
public class EditCourseServlet extends HttpServlet {

    private TeacherService teacherService;
    private CategoryService categoryService;
    private CourseService courseService;

    @Override
    public void init() {

        teacherService = new TeacherService();
        categoryService = new CategoryService();
        courseService = new CourseService();
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

            Teacher teacher =
                    teacherService.findByUserId(userId);

            Course course =
                    courseService.findById(courseId);

            // Ownership check
            if (!course.getTeacherId()
                    .equals(teacher.getId())) {

                response.sendError(
                        HttpServletResponse.SC_FORBIDDEN,
                        "You are not allowed to edit this course."
                );
                return;
            }

            List<Category> categories =
                    categoryService.findAll();

            request.setAttribute(
                    "course",
                    course
            );

            request.setAttribute(
                    "categories",
                    categories
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/edit-course.jsp"
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
                    "Error loading edit course page",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load course."
            );
        }
    }

    @Override
    protected void doPost(
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

            Long courseId =
                    Long.parseLong(
                            request.getParameter("id")
                    );

            Long categoryId =
                    Long.parseLong(
                            request.getParameter("categoryId")
                    );

            String title =
                    request.getParameter("title");

            String description =
                    request.getParameter("description");

            BigDecimal price =
                    new BigDecimal(
                            request.getParameter("price")
                    );

            String status =
                    request.getParameter("status");

            Teacher teacher =
                    teacherService.findByUserId(userId);

            Course updatedCourse =
                    courseService.updateCourse(
                            courseId,
                            teacher.getId(),
                            categoryId,
                            title,
                            description,
                            price,
                            status
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses/view?id="
                            + updatedCourse.getId()
            );

        } catch (NumberFormatException e) {

            request.setAttribute(
                    "error",
                    "Invalid course ID, category or price."
            );

            reloadEditPage(
                    request,
                    response
            );

        } catch (IllegalArgumentException e) {

            request.setAttribute(
                    "error",
                    e.getMessage()
            );

            reloadEditPage(
                    request,
                    response
            );

        } catch (Exception e) {

            getServletContext().log(
                    "Error updating course",
                    e
            );

            request.setAttribute(
                    "error",
                    "Unable to update course."
            );

            reloadEditPage(
                    request,
                    response
            );
        }
    }

    private void reloadEditPage(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            Long courseId =
                    Long.parseLong(
                            request.getParameter("id")
                    );

            Course course =
                    courseService.findById(courseId);

            List<Category> categories =
                    categoryService.findAll();

            request.setAttribute(
                    "course",
                    course
            );

            request.setAttribute(
                    "categories",
                    categories
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/edit-course.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            getServletContext().log(
                    "Error reloading edit course page",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to reload course."
            );
        }
    }
}