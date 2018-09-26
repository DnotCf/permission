package com.tang.permission.dto;

import com.tang.permission.model.SysAcl;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

@Getter
@Setter
@ToString
public class AclDto extends SysAcl {

    //权限是否被选中
    public boolean checked=false;

    //是否有权限操作

    public boolean hasAcl = false;

    public static AclDto adpat(SysAcl sysAcl) {
        AclDto aclDto = new AclDto();
        BeanUtils.copyProperties(sysAcl, aclDto);
        return aclDto;
    }

}
