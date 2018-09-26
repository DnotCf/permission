package com.tang.permission.controller;


import com.google.common.collect.Maps;
import com.tang.permission.bean.PageQuery;
import com.tang.permission.common.JSonResult;
import com.tang.permission.mapper.SysRoleMapper;
import com.tang.permission.params.DeptParam;
import com.tang.permission.params.UserParam;
import com.tang.permission.service.SysRoleService;
import com.tang.permission.service.SysTreeAclModuleService;
import com.tang.permission.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Map;

@Controller
@RequestMapping("/sys/user")
public class SysUserController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysTreeAclModuleService sysTreeAclModuleService;

    @RequestMapping("/noAuth.page")
    public ModelAndView noAuthPage() {
        return new ModelAndView("noAuth");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JSonResult svaeUser(UserParam userParam) {

        sysUserService.saveUser(userParam);
        return JSonResult.success("添加用户成功");
    }
    @RequestMapping("/update.json")
    @ResponseBody
    public JSonResult updateDept(UserParam userParam) {

        sysUserService.updateUser(userParam);
        return JSonResult.success("更新用户成功");
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JSonResult deptPage(@RequestParam("deptId") Integer deptId, PageQuery pageQuery) {


        return JSonResult.success(sysUserService.pageList(deptId, pageQuery));
    }


    @RequestMapping("/delete.json")
    @ResponseBody
    public JSonResult delete(@RequestParam("id") Integer id) {

        sysUserService.delete(id);
        return JSonResult.success("删除成功");
    }


    @RequestMapping("/acls.json")
    @ResponseBody
    public JSonResult userAcls(Integer userId) {

        Map<String, Object> map = Maps.newHashMap();
        map.put("acls", sysTreeAclModuleService.userAclsTree(userId));
        map.put("roles", sysRoleService.getRoleListByUserId(userId));

        return JSonResult.success(map);
    }
}
