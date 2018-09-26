package com.tang.permission.service;

import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.model.SysAcl;
import com.tang.permission.model.SysRole;
import com.tang.permission.model.SysUser;
import com.tang.permission.params.AclParam;
import com.tang.permission.params.RoleParam;

import java.util.List;

public interface SysRoleService {

    public void saveAcl(RoleParam roleParam);

    public void updateAcl(RoleParam roleParam);

    public void delete(Integer id);

    public List<SysRole> getListRole();

    public List<SysUser> getUserListByRoleId(Integer rolid);

    public void chageRoleUsers(Integer roleid, List<Integer> usersId);

    public void chageRoleAcls(Integer roleid, List<Integer> aclsId);

    public List<SysRole> getRoleListByUserId(Integer userid);

    public List<SysRole> getRoleListByAclId(Integer aclid);

    public List<SysUser> getUserListByRoleList(List<SysRole> list);
}
