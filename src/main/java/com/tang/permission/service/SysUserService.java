package com.tang.permission.service;

import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.model.SysUser;
import com.tang.permission.params.UserParam;

import java.util.List;

public interface SysUserService {

    public void saveUser(UserParam userParam);

    public void updateUser(UserParam userParam);

    public PageResult pageList(Integer deptId, PageQuery page);

    public void delete(Integer id);

    public SysUser loginByKeyWord(String key);

    public List<SysUser> getAll();
}
