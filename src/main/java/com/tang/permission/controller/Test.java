package com.tang.permission.controller;

import com.tang.permission.common.ApplicationContextHelpler;
import com.tang.permission.common.JSonResult;
import com.tang.permission.exception.PermissionException;
import com.tang.permission.mapper.SysAclModuleMapper;
import com.tang.permission.model.SysAclModule;
import com.tang.permission.params.TestVo;
import com.tang.permission.util.BeanValidator;
import com.tang.permission.util.JSonMapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@Slf4j
public class Test {


    private static Logger logger = LoggerFactory.getLogger(Test.class);
    @RequestMapping("/hello.json")
    @ResponseBody
    public JSonResult hello() {

        logger.info("hello");
        //throw new PermissionException("test permisoo");
        return JSonResult.success("hello.permission");
    }
    @RequestMapping("/validator.json")
    @ResponseBody
    public JSonResult validate(TestVo vo)  {

        logger.info("validator");

        SysAclModuleMapper sysAclModuleMapper = ApplicationContextHelpler.popBean(SysAclModuleMapper.class);
        SysAclModule sysAclModul = sysAclModuleMapper.selectByPrimaryKey(1);
        logger.info(JSonMapper.obj2String(sysAclModul));
        BeanValidator.check(vo);

        return JSonResult.success("test validate");
    }

}
