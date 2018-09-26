package com.tang.permission.service;

import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.model.SysAcl;
import com.tang.permission.params.AclParam;

public interface SysAclService {

    public void saveAcl(AclParam aclParam);

    public void updateAcl(AclParam aclParam);

    public void delete(Integer id);


    PageResult<SysAcl> getPageByAclModuleId(Integer aclModuleId, PageQuery pageQuery);
}
