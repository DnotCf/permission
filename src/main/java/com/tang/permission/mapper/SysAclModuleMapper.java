package com.tang.permission.mapper;

import com.tang.permission.model.SysAclModule;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclModuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAclModule record);

    int insertSelective(SysAclModule record);

    SysAclModule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAclModule record);

    int updateByPrimaryKey(SysAclModule record);

    List<SysAclModule> getChildAclModulelist(String perfix);

    void updateChildLevelList(@Param("list") List<SysAclModule> list);

    int checkExits(@Param("parentId") Integer parentId,@Param("id") Integer id, @Param("name") String name);

    List<SysAclModule> getAllAclModulelist();

    int countByParentId(Integer parentid);

}