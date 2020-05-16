package com.cvte.scm.wip.common.utils;

import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.function.BiConsumer;

/**
 * @author : jf
 * Date    : 2019.11.06
 * Time    : 10:56
 * Email   ：jiangfeng7128@cvte.com
 */
public class ValidateUtils {
    private static final Validator validator = Validation.byProvider(HibernateValidator.class).configure()
            .failFast(true).buildValidatorFactory().getValidator();

    /**
     * 根据对象字段注解验证数据的正确性
     *
     * @param obj 被验证的对象
     * @param <T> 对象类型
     * @return 验证结果，如果有异常，则其中包含错误信息，否则信息为空。
     */
    public static <T> String validate(T obj) {
        BiConsumer<StringBuilder, ? super ConstraintViolation> accumulator = (sb, error) -> sb.append(error.getMessage()).append("；");
        return validator.validate(obj).stream().collect(StringBuilder::new, accumulator, StringBuilder::append).toString();
    }
}
