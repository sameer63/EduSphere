package com.edusphere.controller;

import com.edusphere.dto.StudentProfile;
import com.edusphere.service.StudentService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/student/profile")
public class StudentProfileServlet extends HttpServlet {
    private StudentService studentService;

    @Override
    public void init(){
        studentService=new StudentService();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException, ServletException {
        HttpSession session=request.getSession(false);
        Long userId=(Long) session.getAttribute("userId");

        try {
            Optional<StudentProfile> profile=studentService.getProfile(userId);
            if(profile.isEmpty()){
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        "Student Profile not found");
                return;
            }

            request.setAttribute("profile",profile.get());
            request.getRequestDispatcher("/WEB-INF/views/student/profile.jsp")
                    .forward(request,response);

        }catch (Exception e){
            log("Unable to load student profile",e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

}
