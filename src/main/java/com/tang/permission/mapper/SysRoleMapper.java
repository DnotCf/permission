package com.tang.permission.mapper;

import com.tang.permission.model.SysRole;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRole record);

    int insertSelective(SysRole record);

    SysRole selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRole record);

    int updateByPrimaryKey(SysRole record);

    List<SysRole> getRoleList();

    int checkExits(@Param("id") Integer id,@Param("name") String name);

    List<SysRole> getRoleListByIdList(List<Integer> list);

}