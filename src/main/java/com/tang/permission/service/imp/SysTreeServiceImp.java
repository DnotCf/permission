package com.tang.permission.service.imp;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.tang.permission.dto.SysDeptDto;
import com.tang.permission.mapper.SysDeptMapper;
import com.tang.permission.model.SysDept;
import com.tang.permission.service.SysTreeService;
import com.tang.permission.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Service
public class SysTreeServiceImp implements SysTreeService {

    @Autowired
    private SysDeptMapper sysDeptMapper;

    public List<SysDeptDto> deptTree() {

        List<SysDept> list = sysDeptMapper.getAllDept();
        List<SysDeptDto> dtoList = Lists.newArrayList();
        for (SysDept sysDept : list) {
            dtoList.add(SysDeptDto.adpat(sysDept));
        }

        return deptListToTree(dtoList);
    }

    public List<SysDeptDto> deptListToTree(List<SysDeptDto> list) {
        if (CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        // 存储形式：level -> [dept1,dept2..]
        Multimap<String, SysDeptDto> multimap = ArrayListMultimap.create();

        List<SysDeptDto> rootList = Lists.newArrayList();
        for (SysDeptDto sysDeptDto : list) {
            multimap.put(sysDeptDto.getLevel(), sysDeptDto);
            if (LevelUtil.ROOT.equals(sysDeptDto.getLevel())) {
                rootList.add(sysDeptDto);
            }
        }
        //从小到大排序 第一层级（根层）
        Collections.sort(rootList, new Comparator<SysDeptDto>() {
            public int compare(SysDeptDto o1, SysDeptDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        //递归排序每个root以下的每层
        transformDepTree(rootList, LevelUtil.ROOT, multimap);
        return rootList;
    }

    private void transformDepTree(List<SysDeptDto> list, String level, Multimap<String, SysDeptDto> multimap) {

        for(int i=0;i<list.size();i++) {
            //遍历每个元素
            SysDeptDto sysDeptDto = list.get(i);
            //获取待处理的层级
            String nextLevel = LevelUtil.calculateLevel(level, sysDeptDto.getId());
            //获取处理层级的列表(下一层)
            List<SysDeptDto> tmpDtolist = (List<SysDeptDto>) multimap.get(nextLevel);
            if (CollectionUtils.isNotEmpty(tmpDtolist)) {
                //排序
                Collections.sort(tmpDtolist, new Comparator<SysDeptDto>() {
                    public int compare(SysDeptDto o1, SysDeptDto o2) {
                        return o1.getSeq() - o2.getSeq();
                    }
                });
                //设置下一层部门
                sysDeptDto.setList(tmpDtolist);
                //进入下一层（递归调用）
                transformDepTree(tmpDtolist, nextLevel, multimap);
            }

        }

    }
}
