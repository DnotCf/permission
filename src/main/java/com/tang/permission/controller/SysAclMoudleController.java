package com.tang.permission.controller;

import com.tang.permission.common.JSonResult;
import com.tang.permission.params.AclModuleParam;
import com.tang.permission.service.SysAclModuleService;
import com.tang.permission.service.SysTreeAclModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/sys/aclModule")
public class SysAclMoudleController {

    @Autowired
    private SysAclModuleService sysAclModuleService;
    @Autowired
    private SysTreeAclModuleService sysTreeAclModuleService;

    @RequestMapping("/acl.page")
    public ModelAndView aclModulePage() {
        return new ModelAndView("acl");
    }
    @RequestMapping("/save.json")
    @ResponseBody
    public JSonResult saveAclMoudle(AclModuleParam aclModuleParam) {

        sysAclModuleService.save(aclModuleParam);
        return JSonResult.success("添加权限模块成功");
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JSonResult updateAclMoudle(AclModuleParam aclModuleParam) {

        sysAclModuleService.update(aclModuleParam);

        return JSonResult.success("更新权限模块成功");
    }
    @RequestMapping("/tree.json")
    @ResponseBody
    public JSonResult deptTree() {

        return JSonResult.success(sysTreeAclModuleService.aclModuleTree());
    }
    @RequestMapping("/delete.json")
    @ResponseBody
    public JSonResult deleteDept(Integer id) {
        sysAclModuleService.delete(id);

        return JSonResult.success();
    }
}
