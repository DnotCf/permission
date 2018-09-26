package com.tang.permission.service;

import com.tang.permission.dto.SysAclModuleDto;
import com.tang.permission.dto.SysDeptDto;
import com.tang.permission.model.SysAcl;

import java.util.List;

public interface SysTreeAclModuleService {

    public List<SysAclModuleDto> aclModuleTree();

    public List<SysAclModuleDto> roleTree(Integer roleid);

    public List<SysAclModuleDto> userAclsTree(Integer userid);

}
