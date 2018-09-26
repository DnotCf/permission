package com.tang.permission.service.imp;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.tang.permission.common.RequestHolder;
import com.tang.permission.exception.ParamException;
import com.tang.permission.mapper.SysRoleAclMapper;
import com.tang.permission.mapper.SysRoleMapper;
import com.tang.permission.mapper.SysRoleUserMapper;
import com.tang.permission.mapper.SysUserMapper;
import com.tang.permission.model.SysRole;
import com.tang.permission.model.SysRoleAcl;
import com.tang.permission.model.SysRoleUser;
import com.tang.permission.model.SysUser;
import com.tang.permission.params.AclParam;
import com.tang.permission.params.RoleParam;
import com.tang.permission.service.SysLogService;
import com.tang.permission.service.SysRoleService;
import com.tang.permission.util.BeanValidator;
import com.tang.permission.util.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysRoleServiceImp implements SysRoleService {

    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRoleCoreService sysRoleCoreService;
    @Autowired
    private SysRoleUserMapper sysRoleUserMapper;
    @Autowired
    private SysRoleAclMapper sysRoleAclMapper;
    @Autowired
    private SysUserMapper sysUserMapper;


    @Transactional
    public void saveAcl(RoleParam roleParam) {
        BeanValidator.check(roleParam);
        if (checkExits(roleParam.getId(),roleParam.getName())) {
            throw new ParamException("角色名称已经存在");
        }else {
            SysRole sysRole = SysRole.builder().name(roleParam.getName()).remark(roleParam.getRemark()).status(roleParam.getStatus()).type(roleParam.getType()).build();

            sysRole.setOperator(RequestHolder.getCurrentUser().getUsername());
            sysRole.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
            sysRole.setOperateTime(new Date());
            sysRoleMapper.insertSelective(sysRole);
            sysRoleCoreService.roleLog(null, sysRole);
        }
    }

    private boolean checkExits(Integer id, String name) {

        return sysRoleMapper.checkExits(id, name) > 0;
    }
    @Transactional
    public void updateAcl(RoleParam roleParam) {
        BeanValidator.check(roleParam);
        if (checkExits(roleParam.getId(),roleParam.getName())) {
            throw new ParamException("角色名称已经存在");
        }
        SysRole before = sysRoleMapper.selectByPrimaryKey(roleParam.getId());
        Preconditions.checkNotNull(before, "待更新的角色不存在");
        SysRole after = SysRole.builder().id(roleParam.getId()).name(roleParam.getName()).remark(roleParam.getRemark()).status(roleParam.getStatus()).type(roleParam.getType()).build();

        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        after.setOperateTime(new Date());
        sysRoleMapper.updateByPrimaryKey(after);
        sysRoleCoreService.roleLog(before, after);

    }

    @Override
    @Transactional
    public void delete(Integer id) {
        SysRole before = sysRoleMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(before, "待删除的角色不存在");
        sysRoleMapper.deleteByPrimaryKey(id);
        sysRoleCoreService.roleLog(before, null);
    }

    public List<SysRole> getListRole() {
        return sysRoleMapper.getRoleList();
    }

    @Override
    public List<SysUser> getUserListByRoleId(Integer rolid) {


        return sysRoleCoreService.getUserListByRoleId(rolid);

    }

    @Override
    public void chageRoleUsers(Integer roleid, List<Integer> usersId) {
        List<Integer> orginUsersIdList = sysRoleCoreService.getUsersIdListByRoleId(roleid);
        if (orginUsersIdList.size() == usersId.size()) {
            Set<Integer> orginUserIdSet = Sets.newHashSet(orginUsersIdList);
            Set<Integer> useridSet = Sets.newHashSet(usersId);
            orginUserIdSet.removeAll(useridSet);
            if (CollectionUtils.isEmpty(orginUserIdSet)) {
                return;
            }
        }

        updateRoleUsers(roleid, usersId);
        sysRoleCoreService.roleUserLog(roleid, orginUsersIdList, usersId);
    }

    @Override
    public void chageRoleAcls(Integer roleid, List<Integer> aclsId) {
        List<Integer> orginAclsIdList = sysRoleAclMapper.getAllAclIdByList(Lists.<Integer>newArrayList(roleid));

        if (orginAclsIdList.size() == aclsId.size()) {
            Set<Integer> orginaclIdSet = Sets.newHashSet(orginAclsIdList);
            Set<Integer> aclidSet = Sets.newHashSet(aclsId);
            orginaclIdSet.removeAll(aclidSet);
            if (CollectionUtils.isEmpty(orginaclIdSet)) {
                return;
            }
        }
        updateRoleAcl(roleid, aclsId);
        sysRoleCoreService.roleAclLog(roleid,orginAclsIdList,aclsId);
    }

    private void updateRoleAcl(Integer roleid, List<Integer> aclsId) {

        sysRoleAclMapper.deleteByPrimaryKey(roleid);

        if (CollectionUtils.isEmpty(aclsId)) {
            return;
        }


        List<SysRoleAcl> acls = Lists.newArrayList();
        for (Integer aclid : aclsId) {
            SysRoleAcl sysRoleAcl = SysRoleAcl.builder().roleId(roleid).aclId(aclid).build();
            sysRoleAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
            sysRoleAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
            sysRoleAcl.setOperateTime(new Date());

            acls.add(sysRoleAcl);
        }
        sysRoleAclMapper.batchInsert(acls);

    }

    @Override
    public List<SysRole> getRoleListByUserId(Integer userid) {

        List<Integer> roleListId = sysRoleUserMapper.getRoleIdList(userid);
        if (CollectionUtils.isEmpty(roleListId)) {
            return Lists.newArrayList();
        }

        return sysRoleMapper.getRoleListByIdList(roleListId);
    }

    @Override
    public List<SysRole> getRoleListByAclId(Integer aclid) {

        List<Integer> roleListId = sysRoleAclMapper.getRoleIdListByAclId(aclid);
        if (CollectionUtils.isEmpty(roleListId)) {
            return Lists.newArrayList();
        }

        return sysRoleMapper.getRoleListByIdList(roleListId);
    }

    @Override
    public List<SysUser> getUserListByRoleList(List<SysRole> list) {

        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        List<Integer> roleIdList = list.stream().map(sysRole -> sysRole.getId()).collect(Collectors.toList());
        List<Integer> userIdList = sysRoleUserMapper.getUserIdListByRoleIdList(roleIdList);
        if (CollectionUtils.isEmpty(userIdList)) {
            return Lists.newArrayList();
        }
        return sysUserMapper.getUserByIdList(userIdList);
    }

    @Transactional
    public void updateRoleUsers(Integer roleid, List<Integer> usersId) {

        sysRoleUserMapper.deleteByRoleId(roleid);

        if (CollectionUtils.isEmpty(usersId)) {
            return;
        }


        List<SysRoleUser> userList = Lists.newArrayList();
        for (Integer userid : usersId) {
            SysRoleUser sysRoleUser = SysRoleUser.builder().roleId(roleid).userId(userid).build();
            sysRoleUser.setOperator(RequestHolder.getCurrentUser().getUsername());
            sysRoleUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
            sysRoleUser.setOperateTime(new Date());

            userList.add(sysRoleUser);
        }

        sysRoleUserMapper.batchInsert(userList);

    }
}
