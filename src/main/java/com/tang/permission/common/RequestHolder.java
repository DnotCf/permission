package com.tang.permission.common;

import com.tang.permission.model.SysUser;

import javax.servlet.http.HttpServletRequest;

public class RequestHolder {

    private static final ThreadLocal<SysUser> userHolder = new ThreadLocal<SysUser>();

    private static final ThreadLocal<HttpServletRequest> requestHolder = new ThreadLocal<HttpServletRequest>();

    public static void add(SysUser sysUser) {
        userHolder.set(sysUser);

    }

    public static void add(HttpServletRequest request) {
        requestHolder.set(request);

    }
    public static SysUser getCurrentUser() {
        return userHolder.get();
    }

    public static HttpServletRequest getRequest() {
        return requestHolder.get();
    }

    public static void destory() {
        userHolder.remove();
        requestHolder.remove();
    }
}
