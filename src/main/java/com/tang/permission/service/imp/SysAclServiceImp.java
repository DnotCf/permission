package com.tang.permission.service.imp;

import com.google.common.base.Preconditions;
import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.common.RequestHolder;
import com.tang.permission.exception.PermissionException;
import com.tang.permission.mapper.SysAclMapper;
import com.tang.permission.model.SysAcl;
import com.tang.permission.params.AclParam;
import com.tang.permission.service.SysAclService;
import com.tang.permission.service.SysLogService;
import com.tang.permission.util.BeanValidator;
import com.tang.permission.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Service
public class SysAclServiceImp implements SysAclService {

    @Autowired
    private SysAclMapper sysAclMapper;
    @Autowired
    private SysLogService sysLogService;

    @Transactional
    public void saveAcl(AclParam aclParam) {

        BeanValidator.check(aclParam);
        if (checkExits(aclParam.getAclModuleId(), aclParam.getId(), aclParam.getName())) {
            throw new PermissionException("当前权限模块下存在相同的权限点名称");
        }
        SysAcl sysAcl = SysAcl.builder().aclModuleId(aclParam.getAclModuleId()).name(aclParam.getName()).remark(aclParam.getRemark()).seq(aclParam.getSeq()).status(aclParam.getStatus()).url(aclParam.getUrl()).build();
        sysAcl.setCode(getRadomId());
        sysAcl.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysAcl.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysAcl.setOperateTime(new Date());

        sysAclMapper.insertSelective(sysAcl);
        sysLogService.aclLog(null, sysAcl);
    }

    private String getRadomId() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyymmddhhmmss");
        return simpleDateFormat.format(new Date()) + "_" + (int)Math.random() * 100;
    }

    private boolean checkExits(Integer aclModuleId, Integer id, String name) {

        return sysAclMapper.checkExits(aclModuleId, id, name) > 0;
    }
    @Transactional
    public void updateAcl(AclParam aclParam) {
        BeanValidator.check(aclParam);
        if (checkExits(aclParam.getAclModuleId(), aclParam.getId(), aclParam.getName())) {
            throw new PermissionException("当前权限模块下存在相同的权限点名称");
        }
        SysAcl before = sysAclMapper.selectByPrimaryKey(aclParam.getId()); //todo
        Preconditions.checkNotNull(before, "未找到待修改的权限点");
        SysAcl after = SysAcl.builder().id(aclParam.getId()).aclModuleId(aclParam.getAclModuleId()).name(aclParam.getName()).remark(aclParam.getRemark()).seq(aclParam.getSeq()).status(aclParam.getStatus()).url(aclParam.getUrl()).build();
        after.setCode(getRadomId());
        after.setOperator(RequestHolder.getCurrentUser().getUsername());
        after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        after.setOperateTime(new Date());

        sysAclMapper.updateByPrimaryKeySelective(after);
        sysLogService.aclLog(before, after);

    }

    @Override
    @Transactional
    public void delete(Integer id) {
        SysAcl before = sysAclMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(before, "待删除的权限不存在");
        sysAclMapper.deleteByPrimaryKey(id);
        sysLogService.aclLog(before, null);
    }

    public PageResult<SysAcl> getPageByAclModuleId(Integer aclModuleId, PageQuery pageQuery) {

        BeanValidator.check(pageQuery);

        int count = sysAclMapper.countByAclModuleId(aclModuleId);

        if (count > 0) {
            List<SysAcl> pageList = sysAclMapper.getPageByAclModuleId(aclModuleId, pageQuery);

            return PageResult.<SysAcl>builder().data(pageList).total(count).build();
        }

        return PageResult.<SysAcl>builder().build();
    }
}
