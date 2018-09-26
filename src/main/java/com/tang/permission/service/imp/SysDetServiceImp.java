package com.tang.permission.service.imp;

import com.google.common.base.Preconditions;
import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.common.RequestHolder;
import com.tang.permission.exception.ParamException;
import com.tang.permission.mapper.SysDeptMapper;
import com.tang.permission.mapper.SysUserMapper;
import com.tang.permission.model.SysDept;
import com.tang.permission.model.SysUser;
import com.tang.permission.params.DeptParam;
import com.tang.permission.service.SysDeptService;
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
public class SysDetServiceImp implements SysDeptService {

    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysLogService sysLogService;

    @Transactional
    public void saveDept(DeptParam deptParam) {
        BeanValidator.check(deptParam);
        if (checkExits(deptParam.getParentId(), deptParam.getId(), deptParam.getName())) {
            throw new ParamException("同一层级下存在相同部门");
        }else {
            SysDept sysDept = SysDept.builder().name(deptParam.getName()).remark(deptParam.getRemark()).parentId(deptParam.getParentId()).seq(deptParam.getSeq()).build();
            sysDept.setLevel(LevelUtil.calculateLevel(getLevel(deptParam.getParentId()), deptParam.getParentId()));
            sysDept.setOperator(RequestHolder.getCurrentUser().getUsername());
            sysDept.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
            sysDept.setOperateTime(new Date());
            sysDeptMapper.insertSelective(sysDept);
            sysLogService.deptLog(null, sysDept);
        }

    }
    @Transactional
    public void updateDept(DeptParam deptParam) {

        BeanValidator.check(deptParam);
        SysDept before = sysDeptMapper.selectByPrimaryKey(deptParam.getId());
        Preconditions.checkNotNull(before, "待更新的部门不存在");
        if (checkExits(deptParam.getParentId(), deptParam.getId(), deptParam.getName())) {
            throw new ParamException("同一层级下存在相同部门");
        }else {
            SysDept after = SysDept.builder().id(deptParam.getId()).name(deptParam.getName()).remark(deptParam.getRemark()).parentId(deptParam.getParentId()).seq(deptParam.getSeq()).build();
            after.setLevel(LevelUtil.calculateLevel(getLevel(deptParam.getParentId()), deptParam.getParentId()));
            after.setOperator(RequestHolder.getCurrentUser().getUsername());
            after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
            after.setOperateTime(new Date());
            updateChildLevel(before, after);
            sysLogService.deptLog(before, after);
        }

    }



    @Transactional
    public void updateChildLevel(SysDept before, SysDept after) {

        String befroLevelperfix = before.getLevel();
        String afterLevelperfix = after.getLevel();
        if(!afterLevelperfix.equals(befroLevelperfix)) {
            List<SysDept> childList = sysDeptMapper.getChildDeptLeveLlist(befroLevelperfix);
            if (CollectionUtils.isNotEmpty(childList)) {
                for (SysDept sysDept : childList) {
                    //以beforLevelPerfix前置的进行处理
                    if (sysDept.getLevel().indexOf(befroLevelperfix+".") == 0) {
                        sysDept.setLevel(afterLevelperfix + sysDept.getLevel().substring(befroLevelperfix.length()));
                    }

                }
                sysDeptMapper.updateChildLevelList(childList);
            }
        }
            sysDeptMapper.updateByPrimaryKey(after);



    }

    private String getLevel(Integer deptId) {
        SysDept sysDept = sysDeptMapper.selectByPrimaryKey(deptId);

        if (sysDept == null) {
            return null;
        }else {
            return sysDept.getLevel();
        }
    }

    private boolean checkExits(Integer parentId, Integer deptid, String detpName) {

        int count = sysDeptMapper.checkExits(parentId, detpName, deptid);
        return count > 0;
    }

    @Override
    @Transactional
    public void delete(Integer id) {

        SysDept sysDept = sysDeptMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(sysDept, "未找到当前待删除的部门,无法删除");
        if (sysDeptMapper.countByParentId(sysDept.getId()) > 0) {
            throw new ParamException("当前部门下存在子部门,无法删除");
        }
        if (sysUserMapper.countByDeptId(sysDept.getId()) > 0) {
            throw new ParamException("当前部门下存在用户,无法删除");
        }

        sysDeptMapper.deleteByPrimaryKey(id);

        sysLogService.deptLog(sysDept, null);

    }
}
