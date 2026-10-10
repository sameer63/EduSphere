package com.edusphere.controller;

import com.edusphere.dto.LoginRequest;
import com.edusphere.model.User;
import com.edusphere.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private UserService userService;

    @Override
    public void init(){
         userService=new UserService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response
    )throws ServletException, IOException {
        request.getRequestDispatcher(
                "/WEB-INF/views/auth/login.jsp"
        ).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response
    )throws ServletException,IOException{
        request.setCharacterEncoding("UTF-8");

        String email=request.getParameter("email");
        String password=request.getParameter("password");

        LoginRequest loginRequest=new LoginRequest(
                email,
                password
        );

        try {
            User user=userService.loginUser(
                    loginRequest.getEmail(),
                    loginRequest.getPassword());


            // Rotate the session ID after successful authentication
            HttpSession session = request.getSession(false);

            if (session == null) {
                session = request.getSession(true);
            } else {
                request.changeSessionId();
            }

            // Store only the information required by the application
            session.setAttribute("userId", user.getId());

            session.setAttribute(
                    "userName",
                    user.getName()
            );

            session.setAttribute(
                    "userRole",
                    user.getRole()
            );


            //Redirect based on the role
            switch (user.getRole()){
                case "STUDENT"->
                    response.sendRedirect(request.getContextPath()+"/student/dashboard");
                case "TEACHER"->
                    response.sendRedirect(request.getContextPath()+"/teacher/dashboard");
                case "ADMIN"->
                    response.sendRedirect(request.getContextPath()+"/admin/dashboard");
                default ->
                        response.sendError(
                                HttpServletResponse.SC_FORBIDDEN
                        );
            }

        }catch (IllegalArgumentException e){
                request.setAttribute("error",e.getMessage());
                request.setAttribute("email",email);
                request.getRequestDispatcher("/WEB-INF/views/auth/login.jsp")
                        .forward(request,response);
        }catch (Exception e){
            log("Login failed", e);

            request.setAttribute(
                    "error",
                    "Something went wrong. Please try again."
            );

            request.getRequestDispatcher(
                    "/WEB-INF/views/auth/login.jsp"
            ).forward(request, response);
        }
    }
}
