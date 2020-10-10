package com.cvte.scm.wip.common.utils;

import cn.hutool.core.util.ClassUtil;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import org.apache.ibatis.type.TypeException;
import org.apache.ibatis.type.TypeReference;
import org.springframework.util.StringUtils;

import javax.persistence.Table;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author : jf
 * Date    : 2019.11.10
 * Time    : 21:24
 * Email   ：jiangfeng7128@cvte.com
 */
public class ClassUtils extends ClassUtil {

    /**
     * 获取对象指定字段的值
     *
     * @param source 对象
     * @param field  字段
     * @return 字段值
     */
    public static Object getFieldValue(Object source, Field field) {
        try {
            return setFieldAccessible(field).get(source);
        } catch (IllegalAccessException e) {
            throw new ParamsIncorrectException("字段获取失败：" + field.getName());
        }
    }

    /**
     * 设置对象指定字段的值
     *
     * @param field  字段
     * @param source 对象
     * @param value  值
     */
    public static void setFieldValue(Object source, Field field, Object value) {
        try {
            setFieldAccessible(field).set(source, value);
        } catch (IllegalAccessException e) {
            throw new ParamsIncorrectException("字段注入失败：" + field.getName());
        }
    }

    /**
     * 设置字段为可访问，并返回字段本身。
     *
     * @param field 字段
     * @return 字段本身
     */
    private static Field setFieldAccessible(Field field) {
        if (field != null && !field.isAccessible()) {
            field.setAccessible(true);
        }
        return field;
    }

    /**
     * 判断字段上是否包含指定注解。
     *
     * @param field 字段
     * @param clazz 注解
     * @return 如果字段包含指定注解则返回true，否则返回false。
     */
    public static boolean isAnnotated(Field field, Class<? extends Annotation> clazz) {
        return field.getDeclaredAnnotation(clazz) != null;
    }

    /**
     * 获取包含指定注解的字段。
     */
    public static List<Field> getAnnotatedField(Class<?> clazz, Class<? extends Annotation> annotation) {
        return Arrays.stream(clazz.getDeclaredFields()).parallel()
                .filter(field -> isAnnotated(field, annotation)).collect(Collectors.toList());
    }

    /**
     * 获取指定注解的name值，诸如{@link Table#name()}
     * <p>
     * 执行过程出现任何异常，皆返回null。
     *
     * @param annotatedObj    被注解的对象，诸如字段对象{@link Field}或者类对象{@link Class}
     * @param annotationClass 注解的类对象
     * @return 指定注解的name值。如果对象不存在指定注解，则返回null。
     */
    public static String getNameOnAnnotation(Object annotatedObj, Class<? extends Annotation> annotationClass) {
        try {
            Method method = annotatedObj.getClass().getMethod("getDeclaredAnnotation", Class.class);
            Annotation annotation = (Annotation) method.invoke(annotatedObj, annotationClass);
            return (String) annotation.getClass().getDeclaredMethod("name").invoke(annotation);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 验证类是否包含{@link Table}注解，并且 {@link Table#name()}不能为空字符串，否则，抛出异常。
     */
    public static boolean isValidEntity(Class<?> entityClass) {
        return nonNull(entityClass) && nonNull(entityClass.getDeclaredAnnotation(Table.class))
                && !StringUtils.isEmpty(getNameOnAnnotation(entityClass, Table.class));
    }

    /**
     * 判断类对象是否是整型类对象
     */
    public static boolean isInteger(Class<?> clazz) {
        return clazz == int.class || clazz == Integer.class;
    }

    public static List<Field> getAllDeclaredFields(Class<?> clazz) {
        if (isNull(clazz)) {
            return Collections.emptyList();
        }
        List<Field> fieldList = new LinkedList<>(Arrays.asList(clazz.getDeclaredFields()));
        fieldList.addAll(Arrays.asList(clazz.getSuperclass().getFields()));
        return fieldList;
    }

    public static Object getFiledByAnnotation(Object entity, Class<? extends Annotation> annotationClazz) {
        Field[] fields = entity.getClass().getDeclaredFields();
        for (Field field : fields) {
            Annotation annotation = field.getAnnotation(annotationClazz);
            if (null != annotation) {
                return getFieldValue(entity, field);
            }
        }
        return null;
    }

    public static Type getSuperclassTypeParameter(Class<?> clazz, int index) {
        Type genericSuperclass = clazz.getGenericSuperclass();
        if (genericSuperclass instanceof Class) {
            // try to climb up the hierarchy until meet something useful
            if (TypeReference.class != genericSuperclass) {
                return getSuperclassTypeParameter(clazz.getSuperclass(), index);
            }

            throw new TypeException("'" + clazz + "' extends TypeReference but misses the type parameter. "
                    + "Remove the extension or add a type parameter to it.");
        }

        Type rawType = ((ParameterizedType) genericSuperclass).getActualTypeArguments()[index];
        if (rawType instanceof ParameterizedType) {
            rawType = ((ParameterizedType) rawType).getRawType();
        }

        return rawType;
    }

}