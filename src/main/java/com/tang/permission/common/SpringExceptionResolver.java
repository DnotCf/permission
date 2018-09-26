package com.tang.permission.common;

import com.tang.permission.exception.ParamException;
import com.tang.permission.exception.PermissionException;


import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {

    protected final Logger logger = LoggerFactory.getLogger(SpringExceptionResolver.class);

    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {

        String url = request.getRequestURI().toString();
        ModelAndView mv;
        String defalutmsg = "系统异常";
        // .json .page结尾
        //要球所有要求数据的请求以.json结尾
        if (url.endsWith(".json")) {

            if (ex instanceof PermissionException || ex instanceof ParamException) {
                JSonResult jSonResult = JSonResult.fail(ex.getMessage());
                mv = new ModelAndView("jsonView", jSonResult.toMap());
            } else {
                logger.error("unknow JsonException url:" + url, ex);
                JSonResult jSonResult = JSonResult.fail(defalutmsg);
                mv = new ModelAndView("jsonView", jSonResult.toMap());
            }

        }else if(url.endsWith(".page")){
            //页面异常 返回页面
            //页面请求以.page结尾
            logger.error("unknow PageException url:" + url, ex);
            JSonResult jSonResult = JSonResult.fail(defalutmsg);
            mv = new ModelAndView("exception", jSonResult.toMap());
        }else {
            logger.error("unknow exception url:" + url, ex);
            JSonResult jSonResult = JSonResult.fail(defalutmsg);
            mv = new ModelAndView("jsonView", jSonResult.toMap());
        }


        return mv;
    }

}
