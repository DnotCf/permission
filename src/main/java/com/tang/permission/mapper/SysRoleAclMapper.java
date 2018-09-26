package com.tang.permission.mapper;

import com.tang.permission.model.SysRoleAcl;

import java.util.List;

public interface SysRoleAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleAcl record);

    int insertSelective(SysRoleAcl record);

    SysRoleAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleAcl record);

    int updateByPrimaryKey(SysRoleAcl record);

    List<Integer> getAllAclIdByList(List<Integer> list);

    List<Integer> getRoleIdListByAclId(Integer aclid);

    void batchInsert(List<SysRoleAcl> list);
}