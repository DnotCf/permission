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
public class UserParam {

    private Integer id;

    @NotBlank(message = "用户名不为空")
    @Length(min = 1,max = 20,message = "用户名长度在20个字符以内")
    private String username;

    @NotBlank(message = "电话不为空")
    @Length(min = 1, max = 13, message = "电话长度在13个字符以内")
    private String telephone;

    @NotBlank(message = "邮箱不为空")
    @Length(min = 1,max = 50,message = "邮箱长度在50个字符以内")
    private String mail;

    @NotNull(message = "必须提供用户所在部门")
    private Integer deptId;

    @NotNull
    @Min(value = 0,message = "用户状态不合法")
    @Max(value = 2,message = "用户状态不合法")
    private Integer status;

    @Length(min = 1,max = 200,message = "备注长度在200个字符以内")
    private String remark;
}
