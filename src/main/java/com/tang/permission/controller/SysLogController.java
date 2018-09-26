package com.tang.permission.controller;

import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.common.JSonResult;
import com.tang.permission.params.SearchLogParam;
import com.tang.permission.service.SysLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@Slf4j
@RequestMapping("/sys/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;


    @RequestMapping("/log.page")
    public ModelAndView page() {

        return new ModelAndView("log");
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JSonResult seacrch(SearchLogParam searchLogParam, PageQuery page) {

        return JSonResult.success(sysLogService.searchLogPage(searchLogParam, page));
    }

    @RequestMapping("/recover.json")
    @ResponseBody
    public JSonResult recover(@RequestParam("id") Integer id) {
        sysLogService.recover(id);

        return JSonResult.success();
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JSonResult delete(@RequestParam("id") Integer id) {

        sysLogService.delete(id);
        return JSonResult.success("删除成功");
    }
}
