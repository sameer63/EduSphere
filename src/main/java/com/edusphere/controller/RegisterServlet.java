package com.edusphere.controller;

import com.edusphere.dto.RegisterRequest;
import com.edusphere.model.User;
import com.edusphere.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init(){
        userService=new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response
                  )throws ServletException, IOException{
        request.getRequestDispatcher("/WEB-INF/views/auth/register.jsp")
                .forward(request,response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response
                     )throws ServletException,IOException{
        request.setCharacterEncoding("UTF-8");
        String name=request.getParameter("name");
        String email=request.getParameter("email");
        String password=request.getParameter("password");
        String dateOfBirth = request.getParameter("dateOfBirth");
        String phone = request.getParameter("phone");

        LocalDate parsedDateOfBirth = null;

        if (dateOfBirth != null
                        && !dateOfBirth.isBlank()) {

            try {
                parsedDateOfBirth =
                        LocalDate.parse(dateOfBirth);
            } catch (Exception e) {
                request.setAttribute(
                        "error",
                        "Invalid date of birth."
                );
                request.getRequestDispatcher(
                        "/WEB-INF/views/auth/register.jsp"
                ).forward(
                        request,
                        response
                );
                return;
            }
        }

        RegisterRequest registerRequest=
                new RegisterRequest(
                name,
                email,
                password,
                null,
                        null
        );

        try {
            User user=userService.registerStudent(
                    registerRequest.getName(),
                    registerRequest.getEmail(),
                    registerRequest.getPassword(),
                    parsedDateOfBirth,
                    phone);
            response.sendRedirect(request.getContextPath()+"/login?registered=true");

        }catch (IllegalArgumentException e){
            request.setAttribute("error",e.getMessage());
            request.setAttribute("name",e.getMessage());
            request.setAttribute("email",e.getMessage());
            request.getRequestDispatcher(
                    "/WEB-INF/views/auth/register.jsp"
            ).forward(request, response);

        } catch (Exception e) {
            log("Registration failed",e);
            request.setAttribute("error","Something went wrong. Please try again.");
            request.getRequestDispatcher(
                    "/WEB-INF/views/auth/register.jsp"
            ).forward(request, response);
        }

    }
}
