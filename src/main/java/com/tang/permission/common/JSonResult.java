package com.tang.permission.common;


import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class JSonResult {

    private boolean ret;

    private String msg;

    private Object data;

    public JSonResult(boolean ret) {
        this.ret = ret;
    }

    public static JSonResult success(Object data, String msg) {
        JSonResult jSonResult = new JSonResult(true);
        jSonResult.data = data;
        jSonResult.msg = msg;
        return jSonResult;
    }

    public static JSonResult success(Object data) {
        JSonResult jSonResult = new JSonResult(true);
        jSonResult.data = data;
        return jSonResult;

    }
    public static JSonResult success() {
        JSonResult jSonResult = new JSonResult(true);
        return jSonResult;

    }
    public static JSonResult fail(String msg) {
        JSonResult jSonResult = new JSonResult(false);
        jSonResult.msg = msg;
        return jSonResult;
    }
    public Map<String,Object> toMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ret", ret);
        map.put("msg", msg);
        map.put("data", data);

        return map;

    }
}
