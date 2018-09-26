package com.tang.permission.dto;

import com.google.common.collect.Lists;
import com.tang.permission.model.SysAclModule;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@ToString
public class SysAclModuleDto extends SysAclModule {

    private List<SysAclModuleDto> aclModuleList = Lists.newArrayList();

    //权限点列表
    private List<AclDto> aclDtos = Lists.newArrayList();

    public static SysAclModuleDto adpat(SysAclModule sysAclModule) {
        SysAclModuleDto systDto = new SysAclModuleDto();
        BeanUtils.copyProperties(sysAclModule, systDto);
        return systDto;
    }


}
