package com.tang.permission.service.imp;

import com.google.common.base.Preconditions;
import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.common.RequestHolder;
import com.tang.permission.exception.ParamException;
import com.tang.permission.mapper.SysUserMapper;
import com.tang.permission.model.SysUser;
import com.tang.permission.params.UserParam;
import com.tang.permission.service.SysLogService;
import com.tang.permission.service.SysUserService;
import com.tang.permission.util.BeanValidator;
import com.tang.permission.util.IpUtil;
import com.tang.permission.util.MD5Util;
import com.tang.permission.util.PasswordUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SysUserServiceImp implements SysUserService {


    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysLogService sysLogService;

    @Transactional
    public void saveUser(UserParam userParam) {

        BeanValidator.check(userParam);
        if (checkTelExits(userParam.getId(), userParam.getTelephone())) {
            throw new ParamException("电话号码已存在");
        }
        if (chectEmailExits(userParam.getId(), userParam.getMail())) {
            throw new ParamException("邮箱已存在");
        }
        String password = PasswordUtil.randomPassword();
        password = "123456";
        //加密
        String encrypassword = MD5Util.encrypt(password);
        SysUser sysUser = SysUser.builder().deptId(userParam.getDeptId()).mail(userParam.getMail()).remark(userParam.getRemark()).status(userParam.getStatus()).telephone(userParam.getTelephone()).username(userParam.getUsername()).build();
        sysUser.setPassword(encrypassword);
        sysUser.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest())); //TODO
        sysUser.setOperateTime(new Date());

        //TODO send Email

        sysUserMapper.insertSelective(sysUser);
        sysLogService.userLog(null, sysUser);

    }

    private boolean chectEmailExits(Integer id, String mail) {


        return sysUserMapper.checkEmailExits(id, mail)>0;
    }

    private boolean checkTelExits(Integer id, String telephone) {

        return sysUserMapper.checkTelExits(id, telephone) >0;
    }
    @Transactional
    public void updateUser(UserParam userParam) {
        BeanValidator.check(userParam);
        if (checkTelExits(userParam.getId(), userParam.getTelephone())) {
            throw new ParamException("电话号码已存在");
        }
        if (chectEmailExits(userParam.getId(), userParam.getMail())) {
            throw new ParamException("邮箱已存在");
        }
        SysUser before = sysUserMapper.selectByPrimaryKey(userParam.getId());
        Preconditions.checkNotNull(before, "待更新的用户不存在");
        SysUser sysUser = SysUser.builder().id(userParam.getId()).username(userParam.getUsername()).password(before.getPassword()).deptId(userParam.getDeptId()).mail(userParam.getMail()).remark(userParam.getRemark()).status(userParam.getStatus()).telephone(userParam.getTelephone()).build();
        sysUser.setOperator(RequestHolder.getCurrentUser().getUsername());
        sysUser.setOperateIp(IpUtil.getRemoteIp(RequestHolder.getRequest()));
        sysUser.setOperateTime(new Date());
        sysUserMapper.updateByPrimaryKey(sysUser);

        sysLogService.userLog(before, sysUser);
    }

    @Override
    public PageResult pageList(Integer deptId, PageQuery page) {

        int count = sysUserMapper.countByDeptId(deptId);

        if (count > 0) {

            List<SysUser> list = sysUserMapper.pageListByDeptId(deptId, page);
            if (CollectionUtils.isNotEmpty(list)) {

                return PageResult.<SysUser>builder().total(count).data(list).build();
            }
        }
        return PageResult.<SysUser>builder().build();

    }

    @Override
    @Transactional
    public void delete(Integer id) {
        SysUser before = sysUserMapper.selectByPrimaryKey(id);
        Preconditions.checkNotNull(before, "待删除的用户不存在");
        sysUserMapper.deleteByPrimaryKey(id);
        sysLogService.userLog(before, null);
    }

    public SysUser loginByKeyWord(String key) {
        return sysUserMapper.loginByKeyWord(key);
    }

    @Override
    public List<SysUser> getAll() {
        return sysUserMapper.getAll();
    }

}
