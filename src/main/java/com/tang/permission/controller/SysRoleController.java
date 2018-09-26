package com.tang.permission.controller;


import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tang.permission.common.JSonResult;
import com.tang.permission.dto.SysAclModuleDto;
import com.tang.permission.model.SysUser;
import com.tang.permission.params.RoleParam;
import com.tang.permission.service.SysRoleService;
import com.tang.permission.service.SysTreeAclModuleService;
import com.tang.permission.service.SysUserService;

import com.tang.permission.util.StringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/sys/role")
public class SysRoleController {

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private SysTreeAclModuleService sysTreeAclModuleService;

    @RequestMapping("/role.page")
    public ModelAndView aclModulePage()
    {
        return new ModelAndView("role");
    }

    @RequestMapping("/save.json")
    @ResponseBody
    public JSonResult saveAcl(RoleParam roleParam) {

        sysRoleService.saveAcl(roleParam);
        return JSonResult.success("添加角色成功");
    }

    @RequestMapping("/update.json")
    @ResponseBody
    public JSonResult updateAcl(RoleParam roleParam) {

        sysRoleService.updateAcl(roleParam);
        return JSonResult.success("更新角色点成功");
    }

    @RequestMapping("/delete.json")
    @ResponseBody
    public JSonResult delete(@RequestParam("id") Integer id) {

        sysRoleService.delete(id);
        return JSonResult.success("删除成功");
    }

    @RequestMapping("/list.json")
    @ResponseBody
    public JSonResult listRole() {

        return JSonResult.success(sysRoleService.getListRole());
    }
    @RequestMapping("/roleTree.json")
    @ResponseBody
    public JSonResult roleTree(@RequestParam("roleId") Integer roleId) {
        List<SysAclModuleDto> dtos = sysTreeAclModuleService.roleTree(roleId);

        return JSonResult.success(dtos);
    }

    @RequestMapping("/users.json")
    @ResponseBody
    public JSonResult users(@RequestParam("roleId") Integer roleId) {

        List<SysUser> selectUserList = sysRoleService.getUserListByRoleId(roleId);
        List<SysUser> allUserList = sysUserService.getAll();
        List<SysUser> unselectUserList = Lists.newArrayList();

        Set<Integer> selectIdList = selectUserList.stream().map(sysUser -> sysUser.getId()).collect(Collectors.toSet());

        for (SysUser sysUser : allUserList) {
            if (sysUser.getStatus() == 1 && !selectIdList.contains(sysUser.getId())) {
                unselectUserList.add(sysUser);
            }
        }
        //屏蔽掉状态不为1的已选用户
        //selectUserList = selectUserList.stream().filter(sysUser -> sysUser.getStatus() != 1).collect(Collectors.toList());

        Map<String, List<SysUser>> map = Maps.newHashMap();
        map.put("selected", selectUserList);
        map.put("unselected", unselectUserList);


        return JSonResult.success(map);
    }

    @RequestMapping("/changeUsers.json")
    @ResponseBody
    public JSonResult chageUsers(Integer roleId, @RequestParam(value = "userIds", required = false, defaultValue = "") String userIds) {
        List<Integer> idList = StringUtil.splitToListInt(userIds);
        sysRoleService.chageRoleUsers(roleId, idList);
        return JSonResult.success();
    }
    @RequestMapping("/changeAcls.json")
    @ResponseBody
    public JSonResult chageAcls(Integer roleId, @RequestParam(value = "aclIds", required = false, defaultValue = "") String aclIds) {
        List<Integer> idList = StringUtil.splitToListInt(aclIds);
        sysRoleService.chageRoleAcls(roleId, idList);
        return JSonResult.success();
    }
}
