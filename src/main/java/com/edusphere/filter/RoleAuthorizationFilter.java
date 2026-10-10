package com.edusphere.filter;


import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(
        urlPatterns = {"/student/*","/teacher/*","/admin/*"}
)
public class RoleAuthorizationFilter implements Filter {

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    )throws IOException, ServletException{
        HttpServletRequest httpRequest=(HttpServletRequest) request;
        HttpServletResponse httpResponse=(HttpServletResponse) response;
        HttpSession session=httpRequest.getSession(false);

        if (session == null) {
            httpResponse.sendRedirect(
                    httpRequest.getContextPath()
                            + "/login"
            );
            return;
        }

        String role=(String) session.getAttribute("userRole");
        String requestURI=httpRequest.getRequestURI();
        String contextPath=httpRequest.getContextPath();
        String path=requestURI.substring(contextPath.length());

        boolean authorised=false;
        if(path.startsWith("/student/")&&"STUDENT".equals(role)){
            authorised=true;
        }
        if (path.startsWith("/teacher/") && "TEACHER".equals(role)){
            authorised = true;
        }

        if (path.startsWith("/admin/") && "ADMIN".equals(role)) {
            authorised = true;
        }

        if (!authorised) {
            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "You are not authorised to access this resource."
            );
            return;
        }
        chain.doFilter(
                request,
                response
        );
    }
}
