
package com.edusphere.filter;

import com.edusphere.util.CsrfTokenUtil;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Set;

@WebFilter("/*")
public class CsrfFilter implements Filter {

    private static final Set<String> SAFE_METHODS =
            Set.of("GET", "HEAD", "OPTIONS", "TRACE");

    @Override
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest =
                (HttpServletRequest) request;

        HttpServletResponse httpResponse =
                (HttpServletResponse) response;

        String method = httpRequest.getMethod();

        if (SAFE_METHODS.contains(method)) {

            String path = httpRequest.getServletPath();

            HttpSession session =
                    httpRequest.getSession(false);

            boolean needsAnonymousFormToken =
                    "/login".equals(path)
                            || "/register".equals(path);

            if (session == null && needsAnonymousFormToken) {
                session = httpRequest.getSession(true);
            }

            if (session != null) {
                httpRequest.setAttribute(
                        CsrfTokenUtil.SESSION_ATTRIBUTE,
                        CsrfTokenUtil.getOrCreateToken(session)
                );
            }

            chain.doFilter(request, response);
            return;
        }

        HttpSession session =
                httpRequest.getSession(false);

        String submittedToken =
                httpRequest.getParameter("csrfToken");

        if (!CsrfTokenUtil.isValid(session, submittedToken)) {
            httpResponse.sendError(
                    HttpServletResponse.SC_FORBIDDEN,
                    "Invalid or missing CSRF token."
            );
            return;
        }

        httpRequest.setAttribute(
                CsrfTokenUtil.SESSION_ATTRIBUTE,
                CsrfTokenUtil.getOrCreateToken(session)
        );

        chain.doFilter(request, response);
    }
}
