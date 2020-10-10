package com.cvte.scm.wip.infrastructure.audit.aop;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.audit.AuditEntity;
import com.cvte.scm.wip.common.audit.AuditField;
import com.cvte.scm.wip.common.audit.AuditId;
import com.cvte.scm.wip.common.audit.AuditParentId;
import com.cvte.scm.wip.common.utils.ClassUtils;
import com.cvte.scm.wip.domain.common.audit.entity.WipAuditLogEntity;
import com.cvte.scm.wip.domain.common.audit.repository.WipAuditLogRepository;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.*;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/10/9 15:14
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Aspect
@Component
public class EntityUpdateAop {

    private static final String MODEL_NAME = "modelName";
    private static final String ENTITY_NAME = "entityName";
    private static final String AUDIT_ID = "auditId";
    private static final String AUDIT_PARENT_ID = "auditParentId";
    private static final String OPT_USER = "opt_user";
    private static final String OPT_TIME = "opt_time";

    private WipAuditLogRepository wipAuditLogRepository;

    public EntityUpdateAop(WipAuditLogRepository wipAuditLogRepository) {
        this.wipAuditLogRepository = wipAuditLogRepository;
    }

    @Pointcut("execution(* com.cvte.scm.wip.domain.common.repository.WipBaseRepository+.update*(..))")
    public void audit(){}

    @Around("audit()")
    public Object doBefore(ProceedingJoinPoint pjp) throws Throwable {

        Map<String,Object> originValues = null;
        Map<String,Object> newValues = null;
        Map<String,Object> baseValues = new HashMap<>();

        Object[] args = pjp.getArgs();
        Object parameter = args[0];
        Class clazz = parameter.getClass();
        if (BaseModel.class.isAssignableFrom(clazz)) {
            AuditEntity auditEntity = (AuditEntity)clazz.getAnnotation(AuditEntity.class);
            if (null != auditEntity) {
                // 仓储服务
                WipBaseRepositoryImpl repository = (WipBaseRepositoryImpl)pjp.getTarget();

                // 原数据
                Object auditId = ClassUtils.getFiledByAnnotation(parameter, AuditId.class);
                if (Objects.isNull(auditId)) {
                    throw new ParamsIncorrectException("操作日志记录发生异常, 原因:ID为空");
                }
                Object dbEntity = repository.selectById(auditId);
                originValues = this.collectValue(dbEntity, auditEntity.allColumn());

                // 新数据
                newValues = this.collectValue(parameter, auditEntity.allColumn());

                // 基础(标识)数据
                baseValues.put(MODEL_NAME, auditEntity.modelName());
                baseValues.put(ENTITY_NAME, auditEntity.entityName());
                baseValues.put(AUDIT_ID, auditId);
                baseValues.put(AUDIT_PARENT_ID, ClassUtils.getFiledByAnnotation(parameter, AuditParentId.class));
                this.getUpdateInfo(parameter, baseValues);
            }
        }

        Object result = pjp.proceed();

        Map<String, String> fieldInfo = collectFiledInfo(clazz);
        List<WipAuditLogEntity> auditList = generateAudit(originValues, newValues, baseValues, fieldInfo);
        if (ListUtil.notEmpty(auditList)) {
            String batchId = UUIDUtils.get32UUID();
            auditList.forEach(audit -> audit.setBatchId(batchId));
            wipAuditLogRepository.insertList(auditList);
        }

        return result;
    }

    /**
     * 获取对象的 字段名-字段值 Map
     * @since 2020/10/10 10:18 上午
     * @author xueyuting
     * @param scope 取值范围, 为false时只取有{AuditField}注解的字段
     */
    private Map<String, Object> collectValue(Object obj, boolean scope) {
        Map<String, Object> values = new HashMap<>();
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (scope) {
                values.put(field.getName(), ClassUtils.getFieldValue(obj, field));
            } else {
                if (field.isAnnotationPresent(AuditField.class)) {
                    values.put(field.getName(), ClassUtils.getFieldValue(obj, field));
                }
            }
        }
        return values;
    }

    /**
     * 收集字段详情信息
     * @since 2020/10/10 2:56 下午
     * @author xueyuting
     */
    private Map<String, String> collectFiledInfo(Class clazz) {
        Map<String, String> fieldInfo = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            AuditField auditField = field.getAnnotation(AuditField.class);
            if (Objects.nonNull(auditField)) {
                fieldInfo.put(field.getName(), auditField.fieldName());
            }
        }
        return fieldInfo;
    }

    /**
     * 获取更新人和更新时间
     * @since 2020/10/10 2:58 下午
     * @author xueyuting
     * @param
     */
    private void getUpdateInfo(Object obj, Map<String, Object> baseValues) {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            int fieldNameHash = field.getName().hashCode();
            if (fieldNameHash == 0xf2016894) {
                // updUser
                baseValues.put(OPT_USER, ClassUtils.getFieldValue(obj, field));
            }
            if (fieldNameHash == 0xf200cf96 || fieldNameHash == 0xf1f96c77) {
                // updTime || updDate
                baseValues.put(OPT_TIME, ClassUtils.getFieldValue(obj, field));
            }
        }
    }

    /**
     * 生成操作日志
     * @since 2020/10/10 2:59 下午
     * @author xueyuting
     * @param originValues 原数据
     * @param newValues 新数据
     * @param baseValues 基础(标识)数据
     * @param fieldInfo 字段详情信息
     */
    private List<WipAuditLogEntity> generateAudit(Map<String, Object> originValues, Map<String, Object> newValues, Map<String, Object> baseValues, Map<String, String> fieldInfo) {
        List<WipAuditLogEntity> auditLogList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(originValues) && CollectionUtils.isNotEmpty(newValues) && CollectionUtils.isNotEmpty(baseValues)) {
            for (Map.Entry<String, Object> entry : originValues.entrySet()) {
                WipAuditLogEntity auditLog = new WipAuditLogEntity();

                String fieldName = entry.getKey();
                Object before = entry.getValue();
                Object after = newValues.get(fieldName);
                if (Objects.isNull(after) || after.equals(before)) {
                    // 字段无变更, 跳过当前字段
                    continue;
                }
                if (Objects.nonNull(before)) {
                    auditLog.setBefore(entry.getValue().toString());
                }
                auditLog.setAfter(newValues.get(fieldName).toString());

                auditLog.setId(UUIDUtils.get32UUID())
                        .setField(fieldName)
                        .setFieldName(fieldInfo.get(fieldName))
                        .setOperationType(ChangedTypeEnum.UPDATE.getCode());

                if (Objects.nonNull(baseValues.get(MODEL_NAME))) {
                    auditLog.setModel(baseValues.get(MODEL_NAME).toString());
                }
                if (Objects.nonNull(baseValues.get(ENTITY_NAME))) {
                    auditLog.setEntity(baseValues.get(ENTITY_NAME).toString());
                }
                if (Objects.nonNull(baseValues.get(AUDIT_ID))) {
                    auditLog.setObjectId(baseValues.get(AUDIT_ID).toString());
                }
                if (Objects.nonNull(baseValues.get(AUDIT_PARENT_ID))) {
                    auditLog.setObjectParentId(baseValues.get(AUDIT_PARENT_ID).toString());
                }
                if (Objects.nonNull(baseValues.get(OPT_USER))) {
                    auditLog.setCrtUser(baseValues.get(OPT_USER).toString())
                            .setUpdUser(baseValues.get(OPT_USER).toString());
                }
                if (Objects.nonNull(baseValues.get(OPT_TIME))) {
                    auditLog.setCrtTime((Date)baseValues.get(OPT_TIME))
                            .setUpdTime((Date)baseValues.get(OPT_TIME));
                }
                auditLogList.add(auditLog);
            }
        }
        return auditLogList;
    }

}
