package com.tang.permission.filter;

import com.google.common.base.Splitter;
import com.google.common.collect.Sets;
import com.tang.permission.common.ApplicationContextHelpler;
import com.tang.permission.common.JSonResult;
import com.tang.permission.common.RequestHolder;
import com.tang.permission.model.SysUser;
import com.tang.permission.service.imp.SysRoleCoreService;
import com.tang.permission.util.JSonMapper;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
public class AclControlFilter implements Filter {

    private static Set<String> exclUrlSet =Sets.newConcurrentHashSet();//todo
    private static String noAuthUrl = "/sys/user/noAuth.page";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

        String exclusionUrls = filterConfig.getInitParameter("exclusionUrls");
        List<String> exclUrlList = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(exclusionUrls);
        exclUrlSet = Sets.newConcurrentHashSet(exclUrlList);
        exclUrlSet.add(noAuthUrl);

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        String serveltPath = request.getServletPath();
        Map requestMap = request.getParameterMap();
        if (exclUrlSet.contains(serveltPath)) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }
        SysUser sysUser = RequestHolder.getCurrentUser();
        if (sysUser == null) {
            log.info("someone visit {},but no login,parameter:{}", serveltPath, JSonMapper.obj2String(requestMap));
            noAuth(request, response);
            return;
        }
        SysRoleCoreService coreService = ApplicationContextHelpler.popBean(SysRoleCoreService.class);
        if (!coreService.hasUrlAcl(serveltPath)) {
            log.info("{} visit {} ,but no auth,parameter:{}",JSonMapper.obj2String(sysUser),JSonMapper.obj2String(requestMap));
            noAuth(request, response);
            return;
        }
        filterChain.doFilter(servletRequest,servletResponse);

    }

    private void noAuth(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String servletPath = request.getServletPath();
        if (servletPath.endsWith(".json")) {
            JSonResult ret = JSonResult.fail("没有权限访问,如需访问,请联系管理员");
            response.setHeader("Content-Type","application/json");
            response.getWriter().print(JSonMapper.obj2String(ret));
            return;
        }else {
            cilentRedirect(servletPath, response);
            return;
        }

    }

    private void cilentRedirect(String url, HttpServletResponse response) throws IOException {

        response.setHeader("Content-Type", "text/html");
        response.getWriter().print("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">\n"
                + "<html xmlns=\"http://www.w3.org/1999/xhtml\">\n" + "<head>\n" + "<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF-8\"/>\n"
                + "<title>跳转中...</title>\n" + "</head>\n" + "<body>\n" + "跳转中，请稍候...\n" + "<script type=\"text/javascript\">//<![CDATA[\n"
                + "window.location.href='" + url + "?ret='+encodeURIComponent(window.location.href);\n" + "//]]></script>\n" + "</body>\n" + "</html>\n");

    }

    @Override
    public void destroy() {

    }
}
