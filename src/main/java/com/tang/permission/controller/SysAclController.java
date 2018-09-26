package com.tang.permission.controller;

import com.google.common.collect.Maps;
import com.tang.permission.bean.PageQuery;
import com.tang.permission.common.JSonResult;

import com.tang.permission.model.SysRole;
import com.tang.permission.params.AclParam;
import com.tang.permission.service.SysAclService;
import com.tang.permission.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/sys/acl")
public class SysAclController {

    @Autowired
    private SysAclService aclService;
    @Autowired
    private SysRoleService sysRoleService;

    @RequestMapping("/acl.page")
    public ModelAndView page() {
        return new ModelAndView("acl");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JSonResult saveAcl(AclParam aclParam) {

        aclService.saveAcl(aclParam);
        return JSonResult.success("添加权限点成功");
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JSonResult updateAcl(AclParam aclParam) {

        aclService.updateAcl(aclParam);
        return JSonResult.success("更新权限点成功");
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JSonResult delete(@RequestParam("id") Integer id) {

        aclService.delete(id);
        return JSonResult.success("删除成功");
    }

    @RequestMapping("/page.json")
    @ResponseBody
    public JSonResult list(@RequestParam("aclModuleId") Integer aclModuleId, PageQuery pageQuery) {


        return JSonResult.success(aclService.getPageByAclModuleId(aclModuleId, pageQuery));
    }

    @RequestMapping("/acls.json")
    @ResponseBody
    public JSonResult Acls (Integer aclId) {

        Map<String, Object> map = Maps.newHashMap();
        List<SysRole> roleList = sysRoleService.getRoleListByAclId(aclId);
        map.put("roles", roleList);
        map.put("users", sysRoleService.getUserListByRoleList(roleList));

        return JSonResult.success(map);
    }

}
