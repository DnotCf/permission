package com.tang.permission.service.imp;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tang.permission.bean.LogType;
import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.common.RequestHolder;
import com.tang.permission.dto.SearchLogDto;
import com.tang.permission.exception.ParamException;
import com.tang.permission.exception.PermissionException;
import com.tang.permission.mapper.*;
import com.tang.permission.model.*;
import com.tang.permission.params.SearchLogParam;
import com.tang.permission.service.SysDeptService;
import com.tang.permission.service.SysLogService;
import com.tang.permission.service.SysRoleService;
import com.tang.permission.util.BeanValidator;
import com.tang.permission.util.IpUtil;
import com.tang.permission.util.JSonMapper;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SysLogServiceImp implements SysLogService {

    @Autowired
    private SysLogMapper sysLogMapper;
    @Autowired
    private SysDeptMapper sysDeptMapper;
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysAclModuleMapper sysAclModuleMapper;
    @Autowired
    private SysAclMapper sysAclMapper;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysRoleCoreService sysRoleCoreService;
    @Autowired
    private SysRoleService sysRoleService;



    @Override
    public PageResult<SysLogWithBLOBs> searchLogPage(SearchLogParam param, PageQuery pageQuery) {

        BeanValidator.check(pageQuery);
        SearchLogDto dto = new SearchLogDto();

        dto.setType(param.getType());
        if (StringUtils.isNotBlank(param.getBeforeSeg())) {
            dto.setBeforeSeq("%"+param.getBeforeSeg()+"%");
        }
        if (StringUtils.isNotBlank(param.getAfterSeg())) {
            dto.setAfterSeq("%" + param.getAfterSeg() + "%");
        }
        if (StringUtils.isNotBlank(param.getOperator())) {
            dto.setOperator("%" + param.getOperator() + "%");
        }
        try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                if (StringUtils.isNotBlank(param.getFromTime())) {
                    dto.setFromTime(simpleDateFormat.parse(param.getFromTime()));
                }
                if (StringUtils.isNotBlank(param.getToTime())) {
                    dto.setToTime(simpleDateFormat.parse(param.getToTime()));
            }

            } catch (ParseException e) {
                throw new ParamException("日期格式错误,正确格式为:yyyy-MM-dd HH:mm:ss");
            }
        int count = sysLogMapper.countBySearchDto(dto);

        if (count > 0) {
            List<SysLogWithBLOBs> list = sysLogMapper.getPageListBySearcheDto(dto, pageQuery);
            return PageResult.<SysLogWithBLOBs>builder().total(count).data(list).build();
        }
        return PageResult.<SysLogWithBLOBs>builder().build();

    }

    @Override
    public void delete(Integer id) {
        sysLogMapper.deleteByPrimaryKey(id);
    }


    @Override
    public void recover(Integer logId) {
        SysLogWithBLOBs sysLog= sysLogMapper.selectByPrimaryKey(logId);
        Preconditions.checkNotNull(sysLog, "待还原的记录不存在");
        switch (sysLog.getType()){
            case LogType.TYPE_DEPT:
                SysDept before = sysDeptMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(before, "待还原的部门已经不存在了");
                if (StringUtils.isNotBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())) {
                    throw new PermissionException("新增和删除不做还原");
                }
                SysDept after = JSonMapper.String2Obj(sysLog.getOldValue(), new TypeReference<SysDept>() {
                });
                after.setOperator(RequestHolder.getCurrentUser().getUsername());
                after.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
                after.setOperateTime(new Date());
                sysDeptMapper.insertSelective(after);
                deptLog(before, after);
                break;

            case LogType.TYPE_USER:

                SysUser before2 = sysUserMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(before2, "待还原的用户已经不存在了");
                if (StringUtils.isNotBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())) {
                    throw new PermissionException("新增和删除不做还原");
                }
                SysUser after2 = JSonMapper.String2Obj(sysLog.getOldValue(), new TypeReference<SysUser>() {
                });
                after2.setOperator(RequestHolder.getCurrentUser().getUsername());
                after2.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
                after2.setOperateTime(new Date());
                sysUserMapper.insertSelective(after2);
                userLog(before2, after2);

                break;
            case LogType.TYPE_ACLMOUDE:
                SysAclModule before3 = sysAclModuleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(before3, "待还原的权限模块已经不存在了");
                if (StringUtils.isNotBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())) {
                    throw new PermissionException("新增和删除不做还原");
                }
                SysAclModule after3 = JSonMapper.String2Obj(sysLog.getOldValue(), new TypeReference<SysAclModule>() {
                });
                after3.setOperator(RequestHolder.getCurrentUser().getUsername());
                after3.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
                after3.setOperateTime(new Date());
                sysAclModuleMapper.insertSelective(after3);
                aclModuleLog(before3, after3);

                break;
            case LogType.TYPE_ACL:

                SysAcl before4 = sysAclMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(before4, "待还原的权限已经不存在了");
                if (StringUtils.isNotBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())) {
                    throw new PermissionException("新增和删除不做还原");
                }
                SysAcl after4 = JSonMapper.String2Obj(sysLog.getOldValue(), new TypeReference<SysAcl>() {
                });
                after4.setOperator(RequestHolder.getCurrentUser().getUsername());
                after4.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
                after4.setOperateTime(new Date());
                sysAclMapper.insertSelective(after4);
                aclLog(before4, after4);

                break;
            case LogType.TYPE_ROLE:

                SysRole before5 = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(before5, "待还原的角色已经不存在了");
                if (StringUtils.isNotBlank(sysLog.getNewValue()) || StringUtils.isBlank(sysLog.getOldValue())) {
                    throw new PermissionException("新增和删除不做还原");
                }
                SysRole after5 = JSonMapper.String2Obj(sysLog.getOldValue(), new TypeReference<SysRole>() {
                });
                after5.setOperator(RequestHolder.getCurrentUser().getUsername());
                after5.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
                after5.setOperateTime(new Date());
                sysRoleMapper.insertSelective(after5);
                sysRoleCoreService.roleLog(before5, after5);

                break;
            case LogType.TYPE_ROLE_USER:

                SysRole before6 = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());

                Preconditions.checkNotNull(before6, "待还原的角色不存在");
                List<Integer> after6 = JSonMapper.String2Obj(sysLog.getOldValue(), new TypeReference<List<Integer>>() {
                });
                sysRoleService.chageRoleUsers(sysLog.getTargetId(), after6);

                break;
            case LogType.TYPE_ROLE_ACL:

                SysRole before7 = sysRoleMapper.selectByPrimaryKey(sysLog.getTargetId());
                Preconditions.checkNotNull(before7, "待还原的角色不存在");
                List<Integer> after7 = JSonMapper.String2Obj(sysLog.getOldValue(), new TypeReference<List<Integer>>() {
                });
                sysRoleService.chageRoleAcls(sysLog.getTargetId(), after7);
                break;
            default:;
        }
    }

    @Override
    public void deptLog(SysDept before, SysDept after) {

        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setNewValue(JSonMapper.obj2String(after));
        sysLog.setOldValue(JSonMapper.obj2String(before));
        sysLog.setType(LogType.TYPE_DEPT);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);

        sysLogMapper.insertSelective(sysLog);

    }

    @Override
    public void userLog(SysUser before, SysUser after) {

        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setNewValue(JSonMapper.obj2String(after));
        sysLog.setOldValue(JSonMapper.obj2String(before));
        sysLog.setType(LogType.TYPE_USER);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    @Override
    public void aclModuleLog(SysAclModule before, SysAclModule after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setNewValue(JSonMapper.obj2String(after));
        sysLog.setOldValue(JSonMapper.obj2String(before));
        sysLog.setType(LogType.TYPE_ACLMOUDE);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }

    @Override
    public void aclLog(SysAcl before, SysAcl after) {
        SysLogWithBLOBs sysLog = new SysLogWithBLOBs();
        sysLog.setNewValue(JSonMapper.obj2String(after));
        sysLog.setOldValue(JSonMapper.obj2String(before));
        sysLog.setType(LogType.TYPE_ACL);
        sysLog.setTargetId(after == null ? before.getId() : after.getId());
        sysLog.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysLog.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysLog.setOperateTime(new Date());
        sysLog.setStatus(1);
        sysLogMapper.insertSelective(sysLog);
    }


}
