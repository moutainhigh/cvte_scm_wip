package com.cvte.scm.wip.common.utils;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.ObjectUtils;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author zy
 * @date 2020-03-25 21:54
 **/
public class EmptyObjectUtils {

    private EmptyObjectUtils() {

    }

    public static boolean isEmptyObject(Object obj, List<String> ignoreFields) {

        Class clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {

            if (ignoreFields.contains(field.getName())) {
                continue;
            }

            field.setAccessible(true);
            try {

                Object fieldValue = field.get(obj);
                if (ObjectUtils.isNotNull(fieldValue)
                        && ((obj instanceof Number || obj instanceof Boolean || obj instanceof String || obj instanceof Character)
                        || !isEmptyObject(fieldValue, ignoreFields))
                ) {
                    return false;
                }
            } catch (Exception e) {
                throw new ParamsIncorrectException(e);
            }
        }
        return true;
    }


}
