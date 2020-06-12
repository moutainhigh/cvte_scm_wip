package com.cvte.scm.wip.common.utils;

import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.common.enums.CodeEnum;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zy
 * @date 2020-04-30 16:36
 **/
@Slf4j
public class EnumUtils {

    private EnumUtils() {

    }

    public static <T extends CodeEnum>T getByCode(String code, Class<T> c) {

        T t = getByCodeOrNull(code, c);
        if (ObjectUtils.isNull(t)) {
            log.error("枚举类{}中字典值{}不存在", c.getName(), code);
            throw new SourceNotFoundException("字典值错误，请联系管理员");
        }
        return t;
    }

    public static <T extends CodeEnum>T getByCodeOrNull(String code, Class<T> c) {

        for (T t : c.getEnumConstants()) {

            if (t.getCode().equals(code)) {
                return t;
            }
        }

        return null;
    }



    /**
     * 判断code是否在枚举列表中
     *
     * @param code
     * @param arr
     * @return boolean
     **/
    public static <T extends CodeEnum>boolean isIn(String code, T... arr) {

        if (ObjectUtils.isNull(arr) || arr.length == 0) {
            return false;
        }

        for (int i = 0; i < arr.length; i++) {
            if (arr[i].getCode().equals(code)) {
                return true;
            }
        }

        return false;
    }
}
