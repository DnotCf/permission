package com.tang.permission.service;

import com.tang.permission.model.SysAclModule;
import com.tang.permission.params.AclModuleParam;

public interface SysAclModuleService {
    public void save(AclModuleParam sysAclModule);

    public void update(AclModuleParam param);

    public void delete(Integer id);
}
