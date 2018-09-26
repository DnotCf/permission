package com.tang.permission.service.imp;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.tang.permission.dto.AclDto;
import com.tang.permission.dto.SysAclModuleDto;
import com.tang.permission.dto.SysDeptDto;
import com.tang.permission.mapper.SysAclMapper;
import com.tang.permission.mapper.SysAclModuleMapper;
import com.tang.permission.model.SysAcl;
import com.tang.permission.model.SysAclModule;
import com.tang.permission.service.SysTreeAclModuleService;
import com.tang.permission.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SysTreeAclModuleServiceImp implements SysTreeAclModuleService {

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;
    @Autowired
    private SysRoleCoreService sysRoleCoreService;

    public List<SysAclModuleDto> aclModuleTree() {

        List<SysAclModule> aclModuleList = sysAclModuleMapper.getAllAclModulelist();

        List<SysAclModuleDto> aclModuleDtos = Lists.newArrayList();

        for (SysAclModule sysAclModule : aclModuleList) {
            aclModuleDtos.add(SysAclModuleDto.adpat(sysAclModule));
        }

        return aclModuleListToTree(aclModuleDtos);
    }

    public List<SysAclModuleDto> roleTree(Integer roleid) {

        //获取当前用户的所有权限
        List<SysAcl> aclUserList = sysRoleCoreService.getCurrentUserAclByCache();
        //获取当前角色的权限
        List<SysAcl> aclRoleList = sysRoleCoreService.getRoleAclList(roleid);
        //获取当前所有权限
        List<AclDto> aclDtoList = Lists.newArrayList();
        Set<Integer> idUserList=Sets.newHashSet();
        if (CollectionUtils.isNotEmpty(aclUserList)) {
             idUserList = aclUserList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());
        }

        Set<Integer> idRoleList = aclRoleList.stream().map(sysAcl -> sysAcl.getId()).collect(Collectors.toSet());

        List<SysAcl> aclList = sysRoleCoreService.getAllAcls();
        for (SysAcl sysAcl : aclList) {
            AclDto aclDto = AclDto.adpat(sysAcl);
            if (idUserList.contains(sysAcl.getId())) {
                aclDto.setHasAcl(true);
            }
            if (idRoleList.contains(sysAcl.getId())) {
                aclDto.checked = true;
            }
            aclDtoList.add(aclDto);
        }

        return roleListToTree(aclDtoList);

    }

    //获取指定用户的权限树
    @Override
    public List<SysAclModuleDto> userAclsTree(Integer userid) {

        List<SysAcl> aclUserList = sysRoleCoreService.getCurrentList(userid);
        List<AclDto> aclDtoList = Lists.newArrayList();
        for (SysAcl sysAcl : aclUserList) {
            AclDto aclDto = AclDto.adpat(sysAcl);
                aclDto.setHasAcl(true);
                aclDto.checked = true;
            aclDtoList.add(aclDto);
        }

        return roleListToTree(aclDtoList);
    }

    private List<SysAclModuleDto> roleListToTree(List<AclDto> aclDtoList) {
        if (CollectionUtils.isEmpty(aclDtoList)) {
            return Lists.newArrayList();
        }
        //获取权限模块树
        List<SysAclModuleDto> sysAclModuleDtoList = aclModuleTree();
        Multimap<Integer, AclDto> multimap = ArrayListMultimap.create();
        //将相同模块的权限点放入mulitmap中
        for (AclDto aclDto : aclDtoList) {
            if (aclDto.getStatus() == 1) {
                multimap.put(aclDto.getAclModuleId(), aclDto);
            }
        }
        //将权限点帮定到权限模块
        bindAclToAclModule(sysAclModuleDtoList, multimap);
        return sysAclModuleDtoList;
    }

    private void bindAclToAclModule(List<SysAclModuleDto> sysAclModuleDtoList, Multimap<Integer, AclDto> multimap) {
        if (CollectionUtils.isEmpty(sysAclModuleDtoList)) {
            return;
        }
        for (SysAclModuleDto sysAclModuleDto : sysAclModuleDtoList) {
            Integer aclModuleId = sysAclModuleDto.getId();
            //获取模块id对应的权限点
            List<AclDto> aclDtoList = (List<AclDto>) multimap.get(aclModuleId);
            if (CollectionUtils.isNotEmpty(aclDtoList)) {
                Collections.sort(aclDtoList, new Comparator<AclDto>() {
                    @Override
                    public int compare(AclDto o1, AclDto o2) {
                        return o1.getSeq()-o2.getSeq();
                    }
                });

                sysAclModuleDto.setAclDtos(aclDtoList);

            }
            bindAclToAclModule(sysAclModuleDto.getAclModuleList(), multimap);
        }

    }


    private List<SysAclModuleDto> aclModuleListToTree(List<SysAclModuleDto> aclModuleDtos) {

        if (CollectionUtils.isEmpty(aclModuleDtos)) {
            return Lists.newArrayList();
        }
        Multimap<String, SysAclModuleDto> multimap = ArrayListMultimap.create(); //可以保存相同层级的模块在同一 key中
        List<SysAclModuleDto> rootList = Lists.newArrayList();

        for (SysAclModuleDto sysAclModuleDto : aclModuleDtos) {
            multimap.put(sysAclModuleDto.getLevel(), sysAclModuleDto);
            if (sysAclModuleDto.getLevel().equals(LevelUtil.ROOT)) {
                rootList.add(sysAclModuleDto);
            }
        }
        Collections.sort(rootList, new Comparator<SysAclModuleDto>() {
            public int compare(SysAclModuleDto o1, SysAclModuleDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        transformAclModuleTree(rootList, LevelUtil.ROOT, multimap);

        return rootList;
    }
    //递归生成权限模块树
    private void transformAclModuleTree(List<SysAclModuleDto> rootList, String level, Multimap<String, SysAclModuleDto> multimap) {

        for (int i=0;i<rootList.size();i++) {
            SysAclModuleDto sysAclModuleDto = rootList.get(i);
            String nextLevel = LevelUtil.calculateLevel(sysAclModuleDto.getLevel(), sysAclModuleDto.getId());

            List<SysAclModuleDto> tmpAclModuleList = (List<SysAclModuleDto>) multimap.get(nextLevel);

            if(CollectionUtils.isNotEmpty(tmpAclModuleList)){

                Collections.sort(tmpAclModuleList, new Comparator<SysAclModuleDto>() {
                    public int compare(SysAclModuleDto o1, SysAclModuleDto o2) {
                        return o1.getSeq() - o2.getSeq();
                    }
                });

                sysAclModuleDto.setAclModuleList(tmpAclModuleList);

                transformAclModuleTree(tmpAclModuleList, nextLevel, multimap);

            }

        }

    }
}
