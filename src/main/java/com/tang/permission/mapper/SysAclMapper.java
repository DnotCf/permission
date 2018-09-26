package com.tang.permission.mapper;

import com.tang.permission.bean.PageQuery;
import com.tang.permission.model.SysAcl;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysAclMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysAcl record);

    int insertSelective(SysAcl record);

    SysAcl selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysAcl record);

    int updateByPrimaryKey(SysAcl record);

    int checkExits(@Param("almId") Integer almId, @Param("id") Integer id,@Param("name") String name);

    int countByAclModuleId(Integer id);

    List<SysAcl> getPageByAclModuleId(@Param("aclModuleId") Integer aclModuleId, @Param("page") PageQuery page);

    List<SysAcl> getByIdList(@Param("list") List<Integer> list);

    List<SysAcl> getAll();

    List<SysAcl> getByUrl(String url);


}