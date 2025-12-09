package com.fashion.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String path = request.getRequestURI();

        // Allow public pages
        if (path.startsWith("/login")
                || path.startsWith("/register")
                || path.startsWith("/forgot-password")
                || path.startsWith("/reset-password")
                || path.equals("/")) {
            return true;
        }

        // Allow ALL static resources
        if (path.matches(".*\\.(css|js|png|jpg|jpeg|gif|svg|ico)$")) {
            return true;
        }

        // Check session
        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("userId") != null) {
            return true;  // User is logged in
        }

        // Block access → redirect to login
        response.sendRedirect("/login");
        return false;
    }
}


