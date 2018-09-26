package com.tang.permission.service.imp;

import com.google.common.base.Preconditions;
import com.tang.permission.common.RequestHolder;
import com.tang.permission.exception.ParamException;
import com.tang.permission.mapper.SysAclMapper;
import com.tang.permission.mapper.SysAclModuleMapper;
import com.tang.permission.model.SysAclModule;
import com.tang.permission.params.AclModuleParam;
import com.tang.permission.service.SysAclModuleService;
import com.tang.permission.service.SysLogService;
import com.tang.permission.util.BeanValidator;
import com.tang.permission.util.IpUtil;
import com.tang.permission.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SysAclModuleServiceImp implements SysAclModuleService {

    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;
    @Autowired
    private SysAclMapper sysAclMapper;
    @Autowired
    private SysLogService sysLogService;

    @Transactional
    public void save(AclModuleParam sysAclModule) {

        BeanValidator.check(sysAclModule);
        if (checkExits(sysAclModule.getParentId(), sysAclModule.getId(), sysAclModule.getName())) {
            throw new ParamException("同一模块下存在相同权限");
        }
        SysAclModule sysAclModule1 = SysAclModule.builder().name(sysAclModule.getName()).parentId(sysAclModule.getParentId()).remark(sysAclModule.getRemark()).seq(sysAclModule.getSeq())
                .status(sysAclModule.getStatus()).level(LevelUtil.calculateLevel(getLevel(sysAclModule.getParentId()), sysAclModule.getParentId())).build();
        sysAclModule1.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysAclModule1.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysAclModule1.setOperateTime(new Date());

        sysAclModuleMapper.insertSelective(sysAclModule1);
        sysLogService.aclModuleLog(null, sysAclModule1);

    }
    @Transactional
    public void update(AclModuleParam param) {
        BeanValidator.check(param);
        if (checkExits(param.getParentId(), param.getId(), param.getName())) {
            throw new ParamException("同一模块下存在相同名称的权限模块");
        }
        SysAclModule before = sysAclModuleMapper.selectByPrimaryKey(param.getId());
        Preconditions.checkNotNull(before, "待更新的权限模块不存在");

        SysAclModule after = SysAclModule.builder().name(param.getName()).remark(param.getRemark()).seq(param.getSeq()).status(param.getStatus())
                .level(LevelUtil.calculateLevel(getLevel(param.getParentId()), param.getParentId())).build();
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        after.setOperateTime(new Date());

        updateChildLevel(before, after);
        sysLogService.aclModuleLog(before, after);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(sysAclModule, "未找到当前待删除的模块,无法删除");
        if (sysAclModuleMapper.countByParentId(sysAclModule.getId()) > 0) {
            throw new ParamException("当前模块下存在子模块,无法删除");
        }
        if (sysAclMapper.countByAclModuleId(sysAclModule.getId())> 0) {
            throw new ParamException("当前模块下存在权限点,无法删除");
        }

        sysAclModuleMapper.deleteByPrimaryKey(id);
        sysLogService.aclModuleLog(sysAclModule, null);

    }


    @Transactional
    public void updateChildLevel(SysAclModule before, SysAclModule after) {

        String befroLevelperfix = before.getLevel();
        String afterLevelperfix = after.getLevel();
        if(!afterLevelperfix.equals(befroLevelperfix)) {
            List<SysAclModule> childList = sysAclModuleMapper.getChildAclModulelist(befroLevelperfix);
            if (CollectionUtils.isNotEmpty(childList)) {
                for (SysAclModule sysDept : childList) {
                    //以beforLevelPerfix前置的进行处理
                    if (sysDept.getLevel().indexOf(befroLevelperfix+".") == 0) {
                        sysDept.setLevel(afterLevelperfix + sysDept.getLevel().substring(befroLevelperfix.length()));
                    }

                }
                sysAclModuleMapper.updateChildLevelList(childList);
            }
        }
        sysAclModuleMapper.updateByPrimaryKey(after);

    }

    private String getLevel(Integer aclMoId) {
        SysAclModule sysAclModule = sysAclModuleMapper.selectByPrimaryKey(aclMoId);
        if (sysAclModule == null) {
            return null;
        }else {
            return sysAclModule.getLevel();
        }


    }

    private boolean checkExits(Integer aclMParentId, Integer aclMoid, String name) {
        int count = sysAclModuleMapper.checkExits(aclMParentId, aclMoid, name);
        return count > 0;
    }

}
