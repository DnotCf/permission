package com.tang.permission.dto;

import com.google.common.collect.Lists;
import com.tang.permission.model.SysDept;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

import java.util.List;

@Getter
@Setter
@ToString
public class SysDeptDto extends SysDept {

    private List<SysDeptDto> list = Lists.newArrayList();

    public static SysDeptDto adpat(SysDept sysDept) {
        SysDeptDto sysDeptDto = new SysDeptDto();
        BeanUtils.copyProperties(sysDept, sysDeptDto);
        return sysDeptDto;
    }
}
