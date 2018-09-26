package com.tang.permission.mapper;

import com.tang.permission.model.SysRoleUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysRoleUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysRoleUser record);

    int insertSelective(SysRoleUser record);

    SysRoleUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysRoleUser record);

    int updateByPrimaryKey(SysRoleUser record);

    List<Integer> getRoleIdList(@Param("userid") Integer userid);

    List<Integer> getUserIdByRoleId(Integer roleid);

    void deleteByRoleId(Integer roleid);

    void batchInsert(List<SysRoleUser> list);

    List<Integer> getUserIdListByRoleIdList(List<Integer> list);

}