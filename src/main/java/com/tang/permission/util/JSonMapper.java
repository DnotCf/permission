package com.tang.permission.util;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.ser.impl.SimpleFilterProvider;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSonMapper {
    protected static final Logger logger = LoggerFactory.getLogger(JSonMapper.class);
    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        //config   配置数据为空的属性字段不显示
        objectMapper.disable(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.configure(SerializationConfig.Feature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.setFilters(new SimpleFilterProvider().setFailOnUnknownId(false));
        objectMapper.setSerializationInclusion(JsonSerialize.Inclusion.NON_EMPTY);
    }

    public static <T> String obj2String(T t) {
        if (t == null) {
            return null;
        }
        try {

            return t instanceof String ? (String) t : objectMapper.writeValueAsString(t);
        } catch (Exception e) {
            logger.warn(" object to string Exception,error:{}", e);
            return null;
        }
    }

    public static <T> T String2Obj(String value, TypeReference<T> typeReference) {
        if (value == null || typeReference == null) {
            return null;
        }
        try {

            return (T)(typeReference.getType().equals(String.class) ? value : objectMapper.readValue(value, typeReference));
        } catch (Exception e) {
            logger.warn("parser String to Object Exception,String:{},TypeReferecce<T>:{},error:{}", value, typeReference, e);
            return null;
        }
    }
}
