package com.tang.permission.service.imp;

import com.google.common.collect.Lists;
import com.tang.permission.bean.CachKeyConstans;
import com.tang.permission.bean.LogType;
import com.tang.permission.common.RequestHolder;
import com.tang.permission.mapper.*;
import com.tang.permission.model.SysAcl;
import com.tang.permission.model.SysLogWithBLOBs;
import com.tang.permission.model.SysRole;
import com.tang.permission.model.SysUser;
import com.tang.permission.service.SysCacheService;
import com.tang.permission.util.IpUtil;
import com.tang.permission.util.JSonMapper;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysRoleCoreService {

    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;

    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;

    @Autowired
    private SysAclMapper sysAclMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private SysLogMapper sysLogMapper;

    @Autowired
    private SysCacheService sysCacheService;

    //或取当前用户的所有权限

    public List<SysAcl> getCurrentUserAclList() {

        Integer userid = RequestHolder.getCurrentUser().getId();

        if(checkRole(userid)){
            return getAllAcls();
        }else {
            return getCurrentList(userid);
        }




    }

    //获取角色拥有的权限
    public List<SysAcl> getRoleAclList(Integer roleid) {

        List<Integer> list = Lists.newArrayList(roleid);

        List<Integer> list1 = sysRoleAclMapper.getAllAclIdByList(list);

        if (CollectionUtils.isEmpty(list1)) {
            return Lists.newArrayList();
        }

        return sysAclMapper.getByIdList(list1);
    }


    public List<SysAcl> getCurrentList(Integer userid) {

        List<Integer> roleIdlist = sysRoleUserMapper.getRoleIdList(userid);
        if (CollectionUtils.isEmpty(roleIdlist)) {
            return Lists.newArrayList();
        }
        List<Integer> aclIdlist = sysRoleAclMapper.getAllAclIdByList(roleIdlist);
        if (CollectionUtils.isEmpty(aclIdlist)) {
            return Lists.newArrayList();
        }else {
            return sysAclMapper.getByIdList(aclIdlist);
        }

    }

    public List<Integer> getUsersIdListByRoleId(Integer roleid) {

        return sysRoleUserMapper.getUserIdByRoleId(roleid);
    }

    public List<SysUser> getUserListByRoleId(Integer rolid) {

        List<Integer> idlist = getUsersIdListByRoleId(rolid);
        if (CollectionUtils.isEmpty(idlist)) {
            return Lists.newArrayList();
        }


        return sysUserMapper.getUserByIdList(idlist);

    }

    //判断当前角色是否为超级管理员
    public boolean checkRole(Integer id) {

       //todo
        if (RequestHolder.getCurrentUser().getUsername().equals("admin")) {
            return true;
        }

        return false;
    }

    public boolean hasUrlAcl(String url) {

        if (isSuperAdmin()) {
            return true;
        }
        List<SysAcl> aclList = sysAclMapper.getByUrl(url);
        if (CollectionUtils.isEmpty(aclList)) {
            return true;
        }
        List<SysAcl> currentAcl = getCurrentUserAclByCache(); //缓存到Redis
        if (CollectionUtils.isEmpty(currentAcl)) {
            return false;
        }
        Set<Integer> aclId = currentAcl.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        //判定规则：只要有一个权限点有权限，那么就有权限访问 //todo
        boolean flag = false;
        for (SysAcl sysAcl : aclList) {
            if (sysAcl.getStatus() != 1 || sysAcl == null) {
                continue;
            }
            flag = true;
            if (aclId.contains(sysAcl.getId())) {
                return true;
            }
        }

        if (!flag) {
            return true;
        }
        return false;




    }

    public List<SysAcl> getCurrentUserAclByCache() {
        String userid = String.valueOf(RequestHolder.getCurrentUser().getId());

        String cacheValue = sysCacheService.getFromCache(CachKeyConstans.USER_ACLS, userid);
        if (StringUtils.isBlank(cacheValue)) {
            List<SysAcl> list = getCurrentUserAclList();
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
            sysCacheService.saveCache(JSonMapper.obj2String(list), 60 * 60 * 60*24, CachKeyConstans.USER_ACLS, userid);
            return list;
        }
        return JSonMapper.String2Obj(cacheValue, new TypeReference<List<SysAcl>>() {});
    }

    public List<SysAcl> getAllAcls() {
        String cacheValue = sysCacheService.getFromCache(CachKeyConstans.SYSTEM_ACL, "all_acls");
        if (StringUtils.isBlank(cacheValue)) {
            List<SysAcl> list = sysAclMapper.getAll();
            if (CollectionUtils.isEmpty(list)) {
                return null;
            }
            sysCacheService.saveCache(JSonMapper.obj2String(list), 60 * 60 * 60 * 24, CachKeyConstans.SYSTEM_ACL, "all_acls");
            return list;
        }
        return JSonMapper.String2Obj(cacheValue, new TypeReference<List<SysAcl>>() {});
    }

    private boolean isSuperAdmin() {
        //自定义加的规则 邮箱含哟amdin的为超级管理员 //todo
        //可以从配置中含获取，可以是指定用户，指定角色
        SysUser sysUser = RequestHolder.getCurrentUser();
        if (sysUser.getUsername().contains("admin")) {
            return true;
        }

        return false;//todo
    }

    public void roleLog(SysRole before, SysRole after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setNewValue(JSonMapper.obj2String(after));
        sysLog.setOldValue(JSonMapper.obj2String(before));
        sysLog.setType(LogType.TYPE_ROLE);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }


    public void roleUserLog(int roleId, List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setNewValue(JSonMapper.obj2String(after));
        sysLog.setOldValue(JSonMapper.obj2String(before));
        sysLog.setType(LogType.TYPE_ROLE_USER);
        sysLog.setTargetId(roleId);
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    public void roleAclLog(int roleId,List<Integer> before, List<Integer> after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setNewValue(JSonMapper.obj2String(after));
        sysLog.setOldValue(JSonMapper.obj2String(before));
        sysLog.setType(LogType.TYPE_ROLE_ACL);
        sysLog.setTargetId(roleId);
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }
}
