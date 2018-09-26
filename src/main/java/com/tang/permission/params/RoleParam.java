package com.tang.permission.params;

import com.tang.permission.bean.Mail;
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
public class RoleParam {

    private Integer id;

    @NotBlank(message = "角色名称不为空")
    @Length(min = 2,max = 20,message = "角色名称长度在2-20个字符")
    private String name;

    @Max(value = 1, message = "角色类型不合法")
    @Min(value = 0, message = "角色类型不合法")
    private Integer type = 1;

    @NotNull(message = "角色状态不为空")
    @Max(value = 1, message = "角色状态不合法")
    @Min(value = 0, message = "角色状态不合法")
    private Integer status = 1;

    @Length(max = 200,message = "角色备注在200个字符以内")
    private String remark;

}
