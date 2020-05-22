package com.cvte.scm.wip.common.utils;

import cn.hutool.core.bean.copier.CopyOptions;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.forbiddens.NoOperationRightException;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.github.rholder.retry.*;
import lombok.extern.slf4j.Slf4j;
import tk.mybatis.mapper.entity.Example;

import javax.persistence.Column;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * 实体类对象的工具类。
 *
 * @author : jf
 * Date    : 2020.01.01
 * Time    : 10:48
 * Email   ：jiangfeng7128@cvte.com
 */
@Slf4j
public class EntityUtils {

    private EntityUtils() {
        throw new IllegalAccessError();
    }

    public static final CopyOptions IGNORE_NULL_VALUE_OPTION = CopyOptions.create().setIgnoreNullValue(true);

    /**
     * 写入标准更新用户信息到具体的实体对象字段
     */
    public static void writeStdCrtInfoToEntity(Object entity, String userId) {
        writeStdInfoToEntity(entity, userId, true);
    }

    /**
     * 写入标准创建用户信息到具体的实体对象字段
     */
    public static void writeStdUpdInfoToEntity(Object entity, String userId) {
        writeStdInfoToEntity(entity, userId, false);
    }

    public static void writeCurUserStdUpdInfoToEntity(Object entity) {
        writeStdUpdInfoToEntity(entity, CurrentContext.getCurrentOperatingUser().getId());
    }


    public static void writeCurUserStdCrtInfoToEntity(Object entity) {
        writeStdCrtInfoToEntity(entity, CurrentContext.getCurrentOperatingUser().getId());
    }


    /**
     * 写入标准用户信息到具体的实体对象字段
     *
     * @param entity 具体的实体对象
     * @param userId 用户ID
     * @param isAdd  信息类型，true 表示创建，false 表示更新
     */
    private static void writeStdInfoToEntity(Object entity, String userId, boolean isAdd) {
        if (Objects.isNull(entity)) {
            return;
        }
        /* 这部分的校验算法，依据来源于 {@link String#hashCode()}，主要利用笛卡尔积，以及位冲突算法，效率远胜之前。 */
        Predicate<String> isValid = s -> s.length() == 7 && s.charAt(3) < 'a' && (s.charAt(0) & 9) == 1 && (isAdd || (s.charAt(0) & 4) == 4);
        ClassUtils.getAllDeclaredFields(entity.getClass()).stream().filter(field -> isValid.test(field.getName())).forEach(
                field -> {
                    field.setAccessible(true);
                    try {
                        switch (field.getName().hashCode()) {
                            case 0x3e16712d: // crtHost
                            case 0xf1fb7271: // updHost
                                field.set(entity, HostUtils.getLocalHostAddress());
                                break;
                            case 0x3e1bce52: // crtTime
                            case 0x3e146b33: // crtDate
                            case 0xf200cf96: // updTime
                            case 0xf1f96c77: // updDate
                                field.set(entity, new Date());
                                break;
                            case 0x3e1c6750: // crtUser
                            case 0xf2016894: // updUser
                                field.set(entity, userId);
                                break;
                            default:
                        }
                    } catch (Exception e) {
                        log.error("初始化标准属性时发生异常！{}", field.getName(), e);
                    }
                }
        );
    }

    /**
     * 累加函数调用所返回的信息，如果为快速失败模式，则出现信息，直接返回，否则全部执行并将所有信息返回。
     */
    public static <E> String accumulate(List<E> data, Function<E, String> handle, boolean failFast) {
        if (CollectionUtils.isEmpty(data)) {
            return "数据为空！";
        }
        StringBuilder messages = new StringBuilder();
        for (E t : data) {
            String errorMsg = handle.apply(t);
            if (StringUtils.isNotEmpty(errorMsg)) {
                if (failFast) {
                    return errorMsg;
                }
                messages.append(errorMsg);
            }
        }
        return messages.toString();
    }

    /**
     * 累加函数调用所返回的信息，默认为全累积模式。
     */
    public static <E> String accumulate(List<E> data, Function<E, String> handle) {
        return accumulate(data, handle, false);
    }

    public static String getEntityPrintInfo(Object entity) {
        return Objects.isNull(entity) ? "" : String.join(", ", Arrays.stream(entity.getClass().getDeclaredFields())
                .filter(field -> Objects.nonNull(field.getDeclaredAnnotation(Column.class)))
                .filter(field -> Objects.nonNull(ClassUtils.getFieldValue(entity, field)))
                .map(field -> field.getName() + " = " + ClassUtils.getFieldValue(entity, field))
                .toArray(String[]::new));
    }

    /**
     * 处理错误信息，并返回错误信息本身。
     * <p>
     * 如果处理模式为 {@link ExecutionModeEnum#STRICT} ，则直接抛出错误异常。
     * 如果处理模式为 {@link ExecutionModeEnum#SLOPPY} ，则打印错误信息日志。
     */
    public static String handleErrorMessage(String errorMessage, ExecutionModeEnum mode) {
        if (StringUtils.isNotEmpty(errorMessage)) {
            if (mode == ExecutionModeEnum.STRICT) {
                throw new ParamsIncorrectException(errorMessage);
            }
            log.error(errorMessage);
        }
        return errorMessage;
    }

    /**
     * 获取指定的 {@link Example} 对象。
     *
     * @param clazz    实体类对象
     * @param property 字段属性名
     * @param value    字段值
     * @return Example 对象
     */
    public static Example getExample(Class<?> clazz, String property, String value) {
        Example example = new Example(clazz);
        example.createCriteria().andEqualTo(property, value);
        return example;
    }

    /**
     * 重试执行指定程序
     * <p>
     * 例子：retry("abc"::toUpperCase, 2, "执行大写操作")
     *
     * @param task          一个返回指定类型数据的生产对象
     * @param attemptNumber 重试的次数
     * @param logDesc       错误时程序介绍
     */
    public static <T> T retry(Supplier<T> task, int attemptNumber, String logDesc) {
        T result;
        StringBuffer errorInfo = new StringBuffer();
        @SuppressWarnings({"SpellCheckingInspection", "UnstableApiUsage"})
        Retryer<T> retryer = RetryerBuilder.<T>newBuilder()
                .retryIfExceptionOfType(Exception.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(attemptNumber))
                .withWaitStrategy(WaitStrategies.fixedWait(1000, TimeUnit.MILLISECONDS))
                .withRetryListener(new RetryListener() {
                    @Override
                    public <V> void onRetry(Attempt<V> attempt) {
                        if (attempt.hasException()) {
                            log.info(StringUtils.format("第 {} 次 {} 失败。", attempt.getAttemptNumber(), logDesc));
                            if (attempt.getAttemptNumber() == 1) {
                                log.error(attempt.getExceptionCause().getMessage());
                            } else {
                                errorInfo.append(attempt.getExceptionCause().getMessage());
                            }
                        }
                    }
                })
                .build();
        try {
            result = retryer.call(task::get);
        } catch (Exception e) {
            throw new NoOperationRightException(errorInfo.toString());
        }
        return result;
    }

    public static String getWipUserId() {
        return CurrentContextUtils.getOrDefaultUserId("SCM-WIP");
    }
}