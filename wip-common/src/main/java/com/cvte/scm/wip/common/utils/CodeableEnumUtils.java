package com.cvte.scm.wip.common.utils;


import java.util.Arrays;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

/**
 * 实现{@link Codeable}接口的枚举工具类。
 *
 * @author : jf
 * Date    : 2020.01.01
 * Time    : 10:20
 * Email   ：jiangfeng7128@cvte.com
 */
public class CodeableEnumUtils {

    private CodeableEnumUtils() {
        throw new IllegalAccessError();
    }

    /**
     * 根据参数 {@param code} 获取实现了 {@link Codeable} 接口的枚举值。
     * <p>
     * 比较 {@param code} 与 {@link Codeable#getCode()}，如果为字符串，比较时忽略大小写，否则调 {@link Object#equals(Object)} 判断。
     */
    public static <T extends Enum & Codeable> T getCodeableEnumByCode(Object code, Class<T> enumClass) {
        BiPredicate<Object, Object> equalsIgnoreCase = (o1, o2) -> ((String) o1).equalsIgnoreCase(o2.toString());
        Predicate<T> equalsByCode = t -> ((t.getCode() instanceof String) && equalsIgnoreCase.test(t.getCode(), code))
                || code.equals(t.getCode());
        if (nonNull(code) && nonNull(enumClass) && nonNull(enumClass.getEnumConstants())) {
            return Arrays.stream(enumClass.getEnumConstants()).filter(equalsByCode).findAny().orElse(null);
        }
        return null;
    }

    public static <T extends Enum & Codeable> boolean isValid(Object code, Class<T> clazz) {
        return getCodeableEnumByCode(code, clazz) != null;
    }

    public static <T extends Enum & Codeable> boolean inValid(Object code, Class<T> clazz) {
        return getCodeableEnumByCode(code, clazz) == null;
    }

    /**
     * 根据位标志 {@param flag} 获取指定顺序位的枚举 {@link Codeable#getCode()} 值列表
     *
     * @param flag  位标志值
     * @param clazz 枚举类对象
     */
    @SuppressWarnings("Convert2MethodRef")
    public static <T extends Enum & Codeable> List<Object> getCodesByOrdinalFlag(int flag, Class<T> clazz) {
        /* 因为值捕获的缘故，map 中的 Lambda 语句不能改为方法引用的形式。 */
        return Arrays.stream(clazz.getEnumConstants()).filter(e -> ((flag >>> e.ordinal()) & 1) == 1)
                .map(e -> e.getCode()).collect(Collectors.toList());
    }
}