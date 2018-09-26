package com.tang.permission.common;

import com.tang.permission.util.JSonMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Slf4j
public class HttpIterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(HttpIterceptor.class);


    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getRequestURI().toString();
        Map map = request.getParameterMap();
        long t = System.currentTimeMillis();
        request.setAttribute("t", t);
        logger.info("request start url:{},param:{}", url, JSonMapper.obj2String(map));

        return true;


    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {


    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String url = request.getRequestURI().toString();
        Map map = request.getParameterMap();
        long t = (Long) request.getAttribute("t");
        long t1 = System.currentTimeMillis();
        logger.info("request complate url:{},param:{}", url, t1 - t);

        removeTheradLocalInfo();
    }

    public void removeTheradLocalInfo() {
        RequestHolder.destory();
    }
}
