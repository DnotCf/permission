package com.tang.permission.controller;


import com.tang.permission.bean.PageQuery;
import com.tang.permission.common.JSonResult;
import com.tang.permission.params.DeptParam;
import com.tang.permission.service.SysDeptService;
import com.tang.permission.service.SysTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sys/dept")
public class SysDeptController {


    @Autowired
    private SysDeptService sysDeptService;
    @Autowired
    private SysTreeService sysTreeService;

    @RequestMapping("/dept.page")
    public ModelAndView deptPage() {
        return new ModelAndView("dept");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JSonResult saveDept(DeptParam deptParam) {
        sysDeptService.saveDept(deptParam);

        return JSonResult.success("添加部门成功");
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JSonResult updateDept(DeptParam deptParam) {
        sysDeptService.updateDept(deptParam);

        return JSonResult.success("更新部门成功");
    }
    @RequestMapping("/tree.json")
    @ResponseBody
    public JSonResult deptTree() {

        return JSonResult.success(sysTreeService.deptTree());
    }


    @RequestMapping("/delete.json")
    @ResponseBody
    public JSonResult deleteDept(Integer id) {
        sysDeptService.delete(id);
        return JSonResult.success();
    }
}
