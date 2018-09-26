package com.tang.permission.controller;

import com.tang.permission.model.SysUser;
import com.tang.permission.service.SysUserService;
import com.tang.permission.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class UserController {

    @Autowired
    private SysUserService sysUserService;

    @RequestMapping("/logout.page")
    public void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //注销session
        request.getSession().invalidate();

        response.sendRedirect("/login.jsp");
    }




    @RequestMapping("/login.page")
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        SysUser sysUser = sysUserService.loginByKeyWord(username);

        String msg="";

        //返回的页面
        String ret = request.getParameter("ret");

        if (StringUtils.isBlank(username)) {
            msg = "用户名不能为空";
        } else if (StringUtils.isBlank(password)) {
            msg = "密码不为空";
        } else if (sysUser == null) {
            msg = "未找到当前用户";
        } else if (!sysUser.getPassword().equals(MD5Util.encrypt(password))) {
            msg = "用户名或密码错误";
        } else if (sysUser.getStatus() != 1) {
            msg = "账号已被冻结请联系管理员";
        }else {
            request.getSession().setAttribute("user", sysUser);
            if (StringUtils.isNotBlank(ret)) {
                response.sendRedirect(ret);
            }else {
                response.sendRedirect("/admin/index.page");
                return;
            }
        }
        request.setAttribute("error", msg);
        request.setAttribute("username", username);
        if (StringUtils.isNotBlank(ret)) {
            request.setAttribute("ret", ret);
        }
        request.getRequestDispatcher("/login.jsp").forward(request, response);

    }
}
