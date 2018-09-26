package com.tang.permission.filter;


import com.tang.permission.common.RequestHolder;
import com.tang.permission.model.SysUser;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class LoginFilter implements Filter {

    public void init(FilterConfig filterConfig) throws ServletException {

    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        SysUser user = (SysUser) request.getSession().getAttribute("user");
        if (user == null) {
            response.sendRedirect("/login.jsp");
            return;
        }
        AtomicInteger integer = new AtomicInteger();
        integer.incrementAndGet();
        RequestHolder.add(user);
        RequestHolder.add(request);

        filterChain.doFilter(servletRequest, servletResponse);
    }

    public void destroy() {

    }
}
