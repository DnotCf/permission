package com.tang.permission.util;


import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tang.permission.exception.ParamException;
import org.apache.commons.collections.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

public class BeanValidator {

    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();

    //校验普通字段
    public static <T> Map<String, String> validate(T t, Class... args) {
        Validator validator = validatorFactory.getValidator();
        Set validateResult = validator.validate(t, args);

        if (validateResult.isEmpty()) {
            return Collections.emptyMap();
        }else {
            LinkedHashMap<String, String> error = Maps.newLinkedHashMap(); //new LinkedHashMap<String, String>();
            Iterator iterator = validateResult.iterator();
            while (iterator.hasNext()) {
                ConstraintViolation constraintViolation = (ConstraintViolation) iterator.next();
                error.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
            }
            return error;

        }
    }

    //校验集合list
    public static Map<String, String> validatorList(Collection<?> collection) {
        //校验是否为空 空抛出异常
        Preconditions.checkNotNull(collection);
        Iterator iterator = collection.iterator();
        Map errors;
        do {
            if (!iterator.hasNext()) {
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            errors = validate(object, new Class[0]);
        } while (errors.isEmpty());
        return errors;
    }

    //整合
    public static Map<String, String> validateObject(Object first, Object... objects) {

        if (objects != null && objects.length > 0) {
            return validatorList(Lists.asList(first, objects));
        }else {
            return validate(first, new Class[0]);
        }
    }

    public static void check(Object first, Object... objects) throws ParamException {
        Map<String, String> error = BeanValidator.validateObject(first, objects);
        if (MapUtils.isNotEmpty(error)) {
            throw new ParamException(error.toString());
        }
    }
}
