package com.tang.permission.mapper;

import com.tang.permission.bean.PageQuery;
import com.tang.permission.dto.SearchLogDto;
import com.tang.permission.model.SysLog;
import com.tang.permission.model.SysLogWithBLOBs;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface SysLogMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SysLogWithBLOBs record);

    int insertSelective(SysLogWithBLOBs record);

    SysLogWithBLOBs selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SysLogWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(SysLogWithBLOBs record);

    int updateByPrimaryKey(SysLog record);

    int countBySearchDto(@Param("dto") SearchLogDto dto);

    //
    List<SysLogWithBLOBs> getPageListBySearcheDto(@Param("dto") SearchLogDto dto, @Param("page") PageQuery page);
}