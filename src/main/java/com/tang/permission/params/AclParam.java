package com.tang.permission.params;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@ToString
public class AclParam {

    private Integer id;

    @NotBlank(message = "权限点名称不为空")
    @Length(min = 2,max = 20,message = "权限点名称长度在2-20个字符")
    private String name;

    @NotNull(message = "必须指定模块")
    private Integer aclModuleId;

    @Length(min = 6,max = 100,message = "权限点url长度必须在6-100个字符")
    private String url;

    @NotNull(message = "必须指定类型")
    @Max(value = 3,message = "权限点类型参数不合法")
    @Min(value = 1,message = "权限点类型参数不合法")
    private Integer type;

    @NotNull(message = "必须指定状态")
    @Max(value = 1,message = "权限点状态参数不合法")
    @Min(value = 0,message = "权限点状态参数不合法")
    private Integer status;

    @NotNull(message = "权限点展示顺序的序号不为空")
    private Integer seq;

    @Length(max = 200,message = "权限点备注在200个字符以内")
    private String remark;
}
