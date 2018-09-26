package com.tang.permission.mapper;

import com.tang.permission.bean.PageQuery;
import com.tang.permission.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysUserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysUser record);

    int insertSelective(SysUser record);

    SysUser selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysUser record);

    int updateByPrimaryKey(SysUser record);

    SysUser loginByKeyWord(String key);

    int checkEmailExits(@Param("userid") Integer userid,@Param("mail") String mail);

    int checkTelExits(@Param("userid") Integer userid,@Param("tel") String tel);

    List<SysUser> getUserByIdList(@Param("list") List<Integer> list);

    List<SysUser> getAll();

    int countByDeptId(Integer deptid);

    List<SysUser> pageListByDeptId(@Param("deptId") Integer deptId, @Param("page") PageQuery page);

}