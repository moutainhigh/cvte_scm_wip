package com.cvte.scm.wip.infrastructure.deprecated;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import lombok.Data;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static cn.hutool.core.collection.CollUtil.isNotEmpty;
import static com.cvte.scm.wip.common.utils.ClassUtils.getNameOnAnnotation;
import static com.cvte.scm.wip.common.utils.ClassUtils.isAnnotated;
import static java.lang.String.join;
import static java.util.Objects.isNull;
import static org.slf4j.helpers.MessageFormatter.arrayFormat;
import static org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils.createBatch;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author : jf
 * Date    : 2019.11.09
 * Time    : 22:00
 * Email   ：jiangfeng7128@cvte.com
 */
@Data
@Component
//@SuppressWarnings("unused")
public class BaseBatchMapper {

    private static final Predicate<Field> isValid = field -> isAnnotated(field, Column.class) && !isAnnotated(field, Transient.class);

    private static final String comma = ",";

    private static final String and = " AND ";

    private NamedParameterJdbcTemplate batchTemplate;

    public BaseBatchMapper(NamedParameterJdbcTemplate batchTemplate) {
        this.batchTemplate = batchTemplate;
    }

    /**
     * 执行无返回值的SQL语句
     */
    public void execute(String sql) {
        batchTemplate.getJdbcOperations().execute(sql);
    }

    /**
     * 调用有返回值的存储过程
     */
    public <T> T execute(CallableStatementCreator csc, CallableStatementCallback<T> action) {
        return batchTemplate.getJdbcOperations().execute(csc, action);
    }

    /**
     * 根据指定的主键集合删除数据
     */
    public void delete(Class<?> clazz, Collection<String> idList) {
        String idCondition = Optional.ofNullable(idList).orElse(Collections.emptyList()).stream().filter(StringUtils::hasLength)
                .map(s -> "'".concat(s).concat("'")).collect(Collectors.joining(","));
        if (StringUtils.isEmpty(idCondition)) {
            return;
        }
        String idName = Arrays.stream(clazz.getDeclaredFields()).filter(isValid).filter(f -> isAnnotated(f, Id.class))
                .map(f -> f.getDeclaredAnnotation(Column.class).name()).findAny().orElse(null);
        if (StringUtils.isEmpty(idName)) {
            throw new ParamsIncorrectException("实体类无主键：" + clazz.getCanonicalName());
        }
        String sql = "delete from " + getTableName(clazz) + " where " + idName + " in(" + idCondition + ")";
        batchTemplate.getJdbcOperations().execute(sql);
    }

    /**
     * 批量插入表数据，并返回插入成功的数量，表信息默认取自参数{@param entityList}的首元素。
     */
    public int insert(List<?> entityList) {
        return insert(entityList, null);
    }

    /**
     * 批量更新表数据，并返回更新成功的数量，表信息默认取自参数{@param entityList}的首元素。
     */
    public int update(List<?> entityList) {
        return update(entityList, null, null);
    }

    /**
     * 批量更新表数据，并返回更新成功的数量，表信息取自参数{@param clazz}。
     */
    public int update(List<?> entityList, Class<?> clazz) {
        return update(entityList, clazz, null);
    }

    /**
     * 批量更新指定字段的表数据，并返回更新成功的数量，表信息默认取自参数{@param entityList}的首元素。
     */
    public int update(List<?> entityList, List<String> updateColumns) {
        return update(entityList, null, updateColumns);
    }

    /**
     * 清空指定类的表数据
     */
    public void truncate(Class<?> clazz) {
        execute(String.format("truncate table %s", getTableName(clazz)));
    }

    /**
     * 根据类对象获取数据表名，若无，抛出异常。
     */
    private String getTableName(Class<?> clazz) {
        String tableName = getNameOnAnnotation(clazz, Table.class);
        if (StringUtils.isEmpty(tableName)) {
            throw new ParamsIncorrectException("实体类无映射表：" + clazz.getCanonicalName());
        }
        return tableName;
    }

    /**
     * 批量更新指定字段的表数据，并返回更新成功的数量，表信息取自参数{@param clazz}。
     */
    public int update(List<?> entityList, Class<?> clazz, List<String> updateColumns) {
        if (isEmpty(entityList)) {
            return 0;
        }
        if (isNull(clazz)) {
            clazz = entityList.get(0).getClass();
        }
        Predicate<Field> fieldPredicate = (Field field) -> true;
        if (isNotEmpty(updateColumns)) {
            Set<String> updateColumnSet = new HashSet<>(updateColumns);
            fieldPredicate = (Field field) -> updateColumnSet.contains(field.getName());
        }
        return batchTemplate.batchUpdate(createUpdateSql(clazz, fieldPredicate), createBatch(entityList.toArray())).length;
    }

    /**
     * 批量插入表数据，并返回插入成功的数量，表信息取自参数{@param clazz}。
     */
    public int insert(List<?> entityList, Class<?> clazz) {
        if (isEmpty(entityList)) {
            return 0;
        }
        if (isNull(clazz)) {
            clazz = entityList.get(0).getClass();
        }
        return batchTemplate.batchUpdate(createInsertionSql(clazz), createBatch(entityList.toArray())).length;
    }

    /**
     * 创建更新表信息的SQL语句
     *
     * @param clazz          包含Table注解的特定类对象
     * @param fieldPredicate 字段的过滤方法
     * @return 表更新的SQL语句
     */
    private String createUpdateSql(Class<?> clazz, Predicate<Field> fieldPredicate) {
        String tableName = getTableName(clazz);
        final String sqlTemplate = "UPDATE {} SET {} WHERE {}";
        Function<Field, String> format = field -> getNameOnAnnotation(field, Column.class) + "=:" + field.getName();
        List<String> idParams = new LinkedList<>(), columnParams = new LinkedList<>();
        Arrays.stream(clazz.getDeclaredFields()).filter(isValid).forEach(field -> {
                    if (isAnnotated(field, Id.class)) {
                        idParams.add(format.apply(field));
                    } else if (fieldPredicate.test(field)) {
                        columnParams.add(format.apply(field));
                    }
                }
        );
        if (isEmpty(idParams)) {
            throw new ParamsIncorrectException("实体类无主键：" + clazz.getCanonicalName());
        }
        return arrayFormat(sqlTemplate, new Object[]{tableName, join(comma, columnParams), join(and, idParams)}).getMessage();
    }

    /**
     * 创建新增表信息的SQL语句
     *
     * @param clazz 包含Table注解的特定类对象
     * @return 表新增的SQL语句
     */
    private String createInsertionSql(Class<?> clazz) {
        String tableName = getTableName(clazz);
        final String sqlTemplate = "INSERT INTO {} ({}) VALUES ({})";
        List<String> columnParams = new ArrayList<>(), valueParams = new ArrayList<>();
        for (Field field : clazz.getDeclaredFields()) {
            if (isValid.test(field)) {
                columnParams.add(getNameOnAnnotation(field, Column.class));
                valueParams.add(":" + field.getName());
            }
        }
        return arrayFormat(sqlTemplate, new Object[]{tableName, join(comma, columnParams), join(comma, valueParams)}).getMessage();
    }
}