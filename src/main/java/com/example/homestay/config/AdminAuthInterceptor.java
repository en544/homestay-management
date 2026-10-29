package com.example.homestay.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AdminAuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("adminUser") != null) {
            return true;
        }

        String target = request.getRequestURI();
        if (request.getQueryString() != null) {
            target += "?" + request.getQueryString();
        }
        request.getSession(true).setAttribute("loginRedirect", target);
        response.sendRedirect("/login");
        return false;
    }
}
