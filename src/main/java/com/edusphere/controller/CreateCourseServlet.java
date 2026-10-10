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
import java.sql.SQLException;
import java.util.List;

@WebServlet("/teacher/courses/create")
public class CreateCourseServlet extends HttpServlet {

    private CategoryService categoryService;
    private TeacherService teacherService;
    private CourseService courseService;

    @Override
    public void init() {

        categoryService = new CategoryService();
        teacherService = new TeacherService();
        courseService = new CourseService();
    }

    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            List<Category> categories =
                    categoryService.findAll();

            request.setAttribute("categories", categories);

            request.getRequestDispatcher(
                    "/WEB-INF/views/teacher/create-course.jsp"
            ).forward(request, response);

        } catch (Exception e) {

            getServletContext().log(
                    "Error loading course creation page",
                    e
            );

            response.sendError(
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to load course creation page."
            );
        }
    }

    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        try {

            HttpSession session = request.getSession(false);

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

            /*
             * IMPORTANT:
             * We get teacherId from the logged-in user,
             * NOT from a request parameter.
             */
            Teacher teacher =
                    teacherService.findByUserId(userId);

            Long teacherId = teacher.getId();

            String title =
                    request.getParameter("title");

            String description =
                    request.getParameter("description");

            String categoryIdParameter =
                    request.getParameter("categoryId");

            String priceParameter =
                    request.getParameter("price");

            String status =
                    request.getParameter("status");

            Long categoryId =
                    Long.parseLong(categoryIdParameter);

            BigDecimal price =
                    new BigDecimal(priceParameter);

            Course course =
                    courseService.createCourse(
                            teacherId,
                            categoryId,
                            title,
                            description,
                            price,
                            status
                    );

            response.sendRedirect(
                    request.getContextPath()
                            + "/teacher/courses"
            );

        } catch (NumberFormatException e) {

            try {
                loadFormWithError(
                        request,
                        response,
                        "Invalid category or price."
                );
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } catch (IllegalArgumentException e) {

            try {
                loadFormWithError(
                        request,
                        response,
                        e.getMessage()
                );
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }

        } catch (Exception e) {

            getServletContext().log(
                    "Error creating course",
                    e
            );

            try {
                loadFormWithError(
                        request,
                        response,
                        "Unable to create course. Please try again."
                );
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void loadFormWithError(
            HttpServletRequest request,
            HttpServletResponse response,
            String error)
            throws ServletException, IOException, SQLException {

        List<Category> categories =
                categoryService.findAll();

        request.setAttribute("categories", categories);
        request.setAttribute("error", error);

        request.setAttribute(
                "title",
                request.getParameter("title")
        );

        request.setAttribute(
                "description",
                request.getParameter("description")
        );

        request.setAttribute(
                "price",
                request.getParameter("price")
        );

        request.getRequestDispatcher(
                "/WEB-INF/views/teacher/create-course.jsp"
        ).forward(request, response);
    }
}