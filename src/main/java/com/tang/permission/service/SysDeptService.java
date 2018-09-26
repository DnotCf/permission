package com.tang.permission.service;

import com.tang.permission.bean.PageQuery;
import com.tang.permission.bean.PageResult;
import com.tang.permission.params.DeptParam;

public interface SysDeptService {

    public void saveDept(DeptParam deptParam);
    public void updateDept(DeptParam deptParam);


    public void delete(Integer id);
}
