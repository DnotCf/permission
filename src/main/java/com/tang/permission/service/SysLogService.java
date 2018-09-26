package com.tang.permission.service;

import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.model.*;
import com.tang.permission.params.SearchLogParam;

import java.util.List;

public interface SysLogService {

    PageResult<SysLogWithBLOBs> searchLogPage(SearchLogParam param, PageQuery pageQuery);

    void delete(Integer id);

    void recover(Integer logId);

    void deptLog(SysDept before, SysDept after);

    void userLog(SysUser before, SysUser after);

    void aclModuleLog(SysAclModule before, SysAclModule after);

    void aclLog(SysAcl before, SysAcl after);


}
