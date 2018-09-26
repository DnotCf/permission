package com.tang.permission.mapper;

import com.tang.permission.model.SysDept;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDeptMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysDept record);

    int insertSelective(SysDept record);

    SysDept selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysDept record);

    int updateByPrimaryKey(SysDept record);

    List<SysDept> getAllDept();

    List<SysDept> getChildDeptLeveLlist(@Param("level") String level);

    void updateChildLevelList( List<SysDept> list);

    int checkExits(@Param("parentId") Integer parentId, @Param("name") String name,@Param("id") Integer id);

    int countByParentId(Integer parentId);
}