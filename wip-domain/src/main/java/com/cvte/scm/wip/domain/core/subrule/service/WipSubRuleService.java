package com.cvte.scm.wip.domain.core.subrule.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.enums.BooleanEnum;
import com.cvte.scm.wip.common.enums.Codeable;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.utils.CalendarUtils;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.deprecated.LazyExecution;
import com.cvte.scm.wip.domain.common.deprecated.RestCallUtils;
import com.cvte.scm.wip.domain.common.serial.SerialNoGenerationService;
import com.cvte.scm.wip.domain.common.user.entity.UserBaseEntity;
import com.cvte.scm.wip.domain.common.user.service.UserService;
import com.cvte.scm.wip.domain.common.workflow.service.WorkFlowService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleRepository;
import com.cvte.scm.wip.domain.core.subrule.valueobject.WipSubRuleLotDetailVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleAdaptTypeEnum;
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleReasonTypeEnum;
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author jf
 * @since 2020-02-13
 */
@Slf4j
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipSubRuleService{

    public static final String SPLIT_SYMBOL = ";";

    private static final BiFunction<ChangedTypeEnum, String, String> ERROR_FORMAT = (changeType, errorMessage) ->
            StringUtils.format("{}失败，您填写的{}，请仔细修改后再重试；", changeType.getDesc(), errorMessage);

    private static final String SERIAL_CODE = "SCM_WIP_SERI_SUB_RULE_CODE";

    private static final String FORM_CODE = "wipSubRule";

    /**
     * 一个位标识，用于获取枚举 {@link SubRuleStatusEnum} 中 "草稿、审核以及生效" 的 code 值列表。
     */
    private static final int DRAFT_REVIEW_EFFECTIVE = 7;

    private WipSubRuleRepository wipSubRuleRepository;

    private UserService userService;
    private WorkFlowService workFlowService;
    private WipSubRuleWfService wipSubRuleWfService;
    private WipSubRuleItemService wipSubRuleItemService;
    private WipSubRuleAdaptService wipSubRuleAdaptService;
    private SerialNoGenerationService serialNoGenerationService;

    public WipSubRuleService(WipSubRuleRepository wipSubRuleRepository, UserService userService, WorkFlowService workFlowService, WipSubRuleWfService wipSubRuleWfService, WipSubRuleItemService wipSubRuleItemService,
                             WipSubRuleAdaptService wipSubRuleAdaptService,
                             SerialNoGenerationService serialNoGenerationService) {
        this.wipSubRuleRepository = wipSubRuleRepository;
        this.userService = userService;
        this.workFlowService = workFlowService;
        this.wipSubRuleWfService = wipSubRuleWfService;
        this.wipSubRuleItemService = wipSubRuleItemService;
        this.wipSubRuleAdaptService = wipSubRuleAdaptService;
        this.serialNoGenerationService = serialNoGenerationService;
    }

    public String addOne(WipSubRuleEntity wipSubRule, ExecutionModeEnum eMode) {
        preHandler(wipSubRule);
        if (Objects.nonNull(wipSubRule)) {
            wipSubRule.setRuleId(UUIDUtils.getUUID()).setRuleStatus(SubRuleStatusEnum.DRAFT.getCode())
                    .setRuleNo(serialNoGenerationService.getNextSerialNumberByCode(SERIAL_CODE));
        }
        String errorMessage = universalValidate(wipSubRule, ChangedTypeEnum.ADD);
        if (EntityUtils.handleErrorMessage(errorMessage, eMode).length() > 0) {
            return errorMessage;
        } else if (CollUtil.isNotEmpty(wipSubRuleRepository.selectByRuleNo(wipSubRule.getRuleNo()))) {
            return EntityUtils.handleErrorMessage(ERROR_FORMAT.apply(ChangedTypeEnum.ADD, "规则编号重复了"), eMode);
        }
        afterValidate(wipSubRule, eMode);

        Object itemRuleResult = wipSubRuleItemService.insertOrDelete(wipSubRule);
        if (itemRuleResult instanceof String) {
            return EntityUtils.handleErrorMessage(ERROR_FORMAT.apply(ChangedTypeEnum.ADD, (String) itemRuleResult), eMode);
        }
        Object reviewerResult = wipSubRuleWfService.insertOrDelete(wipSubRule);
        if (reviewerResult instanceof String) {
            return EntityUtils.handleErrorMessage(ERROR_FORMAT.apply(ChangedTypeEnum.ADD, (String) reviewerResult), eMode);
        }

        Object adaptRuleResult = wipSubRuleAdaptService.insertOrDelete(wipSubRule);
        if (adaptRuleResult instanceof String) {
            return EntityUtils.handleErrorMessage(ERROR_FORMAT.apply(ChangedTypeEnum.ADD, (String) adaptRuleResult), eMode);
        }
        EntityUtils.writeStdCrtInfoToEntity(wipSubRule, EntityUtils.getWipUserId());


        wipSubRuleRepository.insert(wipSubRule);
        ((LazyExecution) itemRuleResult).execute();
        ((LazyExecution) adaptRuleResult).execute();
        ((LazyExecution) reviewerResult).execute();
        return "";
    }

    private String afterValidate(WipSubRuleEntity wipSubRule, ExecutionModeEnum eMode) {
        if (SubRuleReasonTypeEnum.CONTACT_LETTER_REPLACE.getCode().equals(wipSubRule.getRuleReasonType())) {

            if (!isGreaterThanOrEqualsToday(wipSubRule.getEnableTime()) || !isGreaterThanOrEqualsToday(wipSubRule.getEnableTime())) {
                return EntityUtils.handleErrorMessage(ERROR_FORMAT.apply(ChangedTypeEnum.ADD, "生失效日期不能取过去日期"), eMode);
            }
        }
        return "";
    }


    private void preHandler(WipSubRuleEntity wipSubRuleEntity) {
        if (SubRuleReasonTypeEnum.CONTACT_LETTER_REPLACE.getCode().equals(wipSubRuleEntity.getRuleReasonType())) {
            wipSubRuleEntity.setScopeMap(new HashMap<>());
        }
    }
    private static boolean isGreaterThanOrEqualsToday(Date date) {
        return ObjectUtils.isNotNull(date) && CalendarUtils.getDateZero(new Date()).compareTo(CalendarUtils.getDateZero(date)) <= 0;
    }

    /**
     * 通用校验方法，主要校验规则的非空字段，规则状态和替代原因的正确性。
     */
    private String universalValidate(WipSubRuleEntity rule, ChangedTypeEnum type) {
        String errorMessage;
        if (Objects.isNull(rule)) {
            return String.format("{}失败，您的数据为空，请仔细修改后再重试；", type.getDesc());
        } else if (StringUtils.isNotEmpty(errorMessage = validateNullColumn(rule))) {
            return String.format("{}失败，您的数据有点小问题，".concat(errorMessage), type.getDesc());
        } else if (CodeableEnumUtils.inValid(rule.getRuleStatus(), SubRuleStatusEnum.class)) {
            return String.format("{}失败，您填写的规则状态与要求不符，请仔细修改后再重试；", type.getDesc());
        } else if (CodeableEnumUtils.inValid(rule.getRuleReasonType(), SubRuleReasonTypeEnum.class)) {
            return String.format("{}失败，您填写的替代原因与要求不符，请仔细修改后再重试；", type.getDesc());
        } else if ((errorMessage = validateCustomerBaseline(rule)).length() > 0) {
            return errorMessage;
        }
        return "";
    }

    /**
     * 物料的客户基线校验，参考KB链接：https://kb.cvte.com/pages/viewpage.action?pageId=172087368。
     */
    private String validateCustomerBaseline(WipSubRuleEntity rule) {
        String organizationId = rule.getOrganizationId();
        List<String[]> subItemNoList = rule.getSubItemNoList();
        Map<String, Map<String, String>> adaptMap = rule.getScopeMap();
        if (CollectionUtils.isEmpty(subItemNoList) || CollectionUtils.isEmpty(adaptMap)) {
            return "";
        }
        List<String> afterItemNoList = subItemNoList.stream().filter(e -> Objects.nonNull(e) && e.length == 2).map(e -> e[1]).collect(Collectors.toList());
        if (afterItemNoList.isEmpty()) {
            return "";
        }
        Map<String, Collection<String>> scopeMap = adaptMap.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> Arrays.asList(e.getValue().get(SubRuleAdaptTypeEnum.EQUAL.getCode()).split(SPLIT_SYMBOL)))
        );
        List<WipSubRuleLotDetailVO> detailList = wipSubRuleAdaptService.getSubRuleLotDetailData(organizationId, scopeMap);
        if (detailList.isEmpty()) {
            return "";
        }
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("itemNumber", String.join(",", afterItemNoList));
        for (WipSubRuleLotDetailVO detail : detailList) {
            paramMap.put("prodNumber", detail.getProductNo());
            JSONObject response = JSON.parseObject(RestCallUtils.callRest(RestCallUtils.RequestMethod.GET, "https://cplmobj.gz.cvte.cn/openapi/baseline/lock_bom", paramMap));
            if (!response.get("status").equals("0")) {
                return String.format("客户基线校验失败，%s", response.get("message"));
            }
            JSONObject data = JSON.parseObject(response.get("data").toString());
            if (data.get("changeFlag").equals("N")) {
                return String.format("物料%s不在产品代码%s的客户基线内，请走 4M1E 变更流程，不允许出临时代用单。",
                        data.get("itemList"), detail.getProductNo());
            }
        }
        return "";
    }

    /**
     * 校验规则表{@code wip_sub_rule}的非空字段。
     */
    private String validateNullColumn(WipSubRuleEntity rule) {
        StringBuilder errorMessage = new StringBuilder();
        BiFunction<String, String, String> format = (s1, s2) -> StringUtils.isEmpty(s1) ? s2 + "为空，" : "";
        errorMessage.append(format.apply(rule.getRuleNo(), "规则编号"))
                .append(format.apply(rule.getOrganizationId(), "组织"))
                .append(format.apply(rule.getRuleStatus(), "规则状态"))
                .append(format.apply(rule.getRuleReasonType(), "规则替换原因"))
                .append(format.apply(rule.getIfPr(), "是否PR"))
                .append(format.apply(rule.getIfMix(), "是否混料"))
                .append(format.apply(rule.getIfCouple(), "是否成套替代"))
                .append(Objects.isNull(rule.getEnableTime()) ? "生效时间为空，" : "")
                .append(format.apply(rule.getIfMrp(), "是否参与MRP计算"));
        if (errorMessage.length() > 0) {
            errorMessage.setCharAt(errorMessage.length() - 1, '；');
        }
        return errorMessage.toString();
    }

    public String update(List<WipSubRuleEntity> wipSubRuleList, ExecutionModeEnum eMode) {
        if (CollectionUtil.isEmpty(wipSubRuleList)) {
            return "待更新的数据为空；";
        }
        List<WipSubRuleEntity> updateData = new ArrayList<>(wipSubRuleList.size());
        String errorMessage = EntityUtils.accumulate(wipSubRuleList, rule -> validateAndGetUpdateData(rule, updateData), eMode == ExecutionModeEnum.STRICT);
        StringBuilder errorMessages = new StringBuilder(EntityUtils.handleErrorMessage(errorMessage, eMode));
        List<LazyExecution> executor = new ArrayList<>();
        Object itemRuleResult, adaptRuleResult, reviewerResult;
        for (WipSubRuleEntity rule : updateData) {
            preHandler(rule);
            String afterValidateMsg = afterValidate(rule, eMode);
            if (StringUtils.isNotBlank(afterValidateMsg)) {
                errorMessages.append(afterValidateMsg);
                continue;
            }

            itemRuleResult = wipSubRuleItemService.insertOrDelete(rule);
            if (itemRuleResult instanceof String) {
                errorMessages.append(EntityUtils.handleErrorMessage(ERROR_FORMAT.apply(ChangedTypeEnum.UPDATE, (String) itemRuleResult), eMode));
                continue;
            }
            reviewerResult = wipSubRuleWfService.insertOrDelete(rule);
            if (reviewerResult instanceof String) {
                errorMessages.append(EntityUtils.handleErrorMessage(ERROR_FORMAT.apply(ChangedTypeEnum.UPDATE, (String) reviewerResult), eMode));
                continue;
            }

            adaptRuleResult = wipSubRuleAdaptService.insertOrDelete(rule);
            if (adaptRuleResult instanceof String) {
                errorMessages.append(EntityUtils.handleErrorMessage(ERROR_FORMAT.apply(ChangedTypeEnum.UPDATE, (String) adaptRuleResult), eMode));
                continue;
            }

            EntityUtils.writeStdUpdInfoToEntity(rule, EntityUtils.getWipUserId());
            executor.add((LazyExecution) reviewerResult);
            executor.add((LazyExecution) itemRuleResult);
            executor.add((LazyExecution) adaptRuleResult);
            executor.add(() -> wipSubRuleRepository.update(rule));
        }
        executor.forEach(LazyExecution::execute);
        return errorMessages.toString();
    }

    public String invalid(ExecutionModeEnum eMode, String... ruleIds) {
        if (ArrayUtil.isEmpty(ruleIds)) {
            return EntityUtils.handleErrorMessage("待删除的数据为空；", eMode);
        }
        List<WipSubRuleEntity> wipSubRuleList = wipSubRuleRepository.selectByRuleIdAndStatus(Arrays.asList(ruleIds), CodeableEnumUtils.getCodesByOrdinalFlag(DRAFT_REVIEW_EFFECTIVE, SubRuleStatusEnum.class));
        Set<String> validRuleIds = wipSubRuleList.stream().map(WipSubRuleEntity::getRuleId).collect(Collectors.toSet());
        String[] invalidRuleIds = Arrays.stream(ruleIds).filter(id -> !validRuleIds.contains(id)).toArray(String[]::new);
        if (ArrayUtil.isNotEmpty(invalidRuleIds)) {
            return EntityUtils.handleErrorMessage("删除错误，您需要删除的数据存在错误，请仔细修改后再重试；", eMode);
        }
        List<String> invalidRuleIdList = new LinkedList<>(), expiredRuleIdList = new LinkedList<>();
        for (WipSubRuleEntity rule : wipSubRuleList) {
            if (CodeableEnumUtils.getCodeableEnumByCode(rule.getRuleStatus(), SubRuleStatusEnum.class) == SubRuleStatusEnum.EFFECTIVE) {
                expiredRuleIdList.add(rule.getRuleId());
            } else {
                invalidRuleIdList.add(rule.getRuleId());
            }
        }
        /* 规则状态为非生效中记录，操作成功后，规则状态改为已作废 */
        setRuleStatus(invalidRuleIdList, () -> new WipSubRuleEntity().setRuleStatus(SubRuleStatusEnum.INVALID.getCode()));
        /* 规则状态为生效中的记录，操作成功后，规则状态改为已失效，且失效日期更新为当前时间 */
        setRuleStatus(expiredRuleIdList, () -> new WipSubRuleEntity().setRuleStatus(SubRuleStatusEnum.EXPIRED.getCode()).setDisableTime(new Date()));
        // 作废审核流
        List<String> abandonIdList = new ArrayList<>(invalidRuleIdList);
        abandonIdList.addAll(expiredRuleIdList);
        for (String ruleId : abandonIdList) {
            if (!"false".equals(workFlowService.isExistProcess(ruleId, FORM_CODE))) {
                workFlowService.abandon(ruleId, FORM_CODE, "作废单据");
            }
        }
        return "";
    }

    /**
     * 根据指定规则ID，设置特定规则状态逻辑。
     *
     * @param ruleIds      特定的规则ID列表
     * @param ruleSupplier 特定的规则状态逻辑
     */
    private void setRuleStatus(List<String> ruleIds, Supplier<WipSubRuleEntity> ruleSupplier) {
        if (CollectionUtil.isNotEmpty(ruleIds)) {
            WipSubRuleEntity wipSubRule = ruleSupplier.get();
            EntityUtils.writeStdUpdInfoToEntity(wipSubRule, EntityUtils.getWipUserId());
            wipSubRuleRepository.updateByRuleId(wipSubRule, ruleIds);
        }
    }

    /**
     * 根据指定的规则ID {@param ruleId} 获取临时代用单及其子表(替换规则、适用规则和审核信息)的所有信息。
     */
    public WipSubRuleEntity getSubRuleData(String ruleId) {
        WipSubRuleEntity wipSubRule;
        if (StringUtils.isEmpty(ruleId) || Objects.isNull(wipSubRule = wipSubRuleRepository.selectByRuleId(ruleId))) {
            throw new ParamsIncorrectException("很抱歉，无法找到指定的临时代用单规则信息。");
        }
        return wipSubRule.setSubItemNoList(wipSubRuleItemService.getSubRuleItemData(ruleId))
                .setScopeMap(wipSubRuleAdaptService.getAdaptRuleData(ruleId))
                .setReviewerMap(wipSubRuleWfService.getReviewerData(ruleId));
    }

    private static final UserBaseEntity EMPTY_SYS_USER = new UserBaseEntity();

    /**
     * 根据指定的规则编号获取临时代用单详细信息，将其中字典字段转换为具体描述。
     */
    public WipSubRuleEntity getSubRuleDetail(String ruleId) {
        WipSubRuleEntity wipSubRule = wipSubRuleRepository.selectByRuleId(ruleId);
        if (wipSubRule == null) {
            log.error(String.format("[scm-wip][sub-rule][rule-detail] 规则 ID 不存在，rule_id = {}。", Optional.ofNullable(ruleId).orElse("null")));
            throw new ParamsIncorrectException("抱歉，系统出现了未知错误，速联系相关人员。");
        }
        /* 审核状态 */
        setDictionaryColumn(wipSubRule::getRuleStatus, wipSubRule::setRuleStatus, SubRuleStatusEnum.class, "rule_status", "规则状态");
        /* 单据类型 */
        setDictionaryColumn(wipSubRule::getRuleReasonType, wipSubRule::setRuleReasonType, SubRuleReasonTypeEnum.class, "rule_reason_type", "规则原因类型");
        /* 是否产生PR */
        setDictionaryColumn(wipSubRule::getIfPr, wipSubRule::setIfPr, BooleanEnum.class, "if_pr", "是否产生 PR ");
        /* 是否混料 */
        setDictionaryColumn(wipSubRule::getIfMix, wipSubRule::setIfMix, BooleanEnum.class, "if_mix", "是否混料");
        /* 是否成套替代 */
        setDictionaryColumn(wipSubRule::getIfCouple, wipSubRule::setIfCouple, BooleanEnum.class, "if_couple", "是否成套替代");
        /* 申请人 */
        wipSubRule.setUpdUser(Optional.ofNullable(userService.getEnableUserInfo(wipSubRule.getUpdUser())).orElse(EMPTY_SYS_USER).getName());
        return wipSubRule;
    }

    /**
     * 将具体字典字段的值转换为有意义的描述
     */
    private <T extends Enum & Codeable> void setDictionaryColumn(Supplier<String> valueSupplier, Consumer<String> setConsumer,
                                                                 Class<T> clazz, String columnName, String columnDesc) {
        String columnValue = valueSupplier.get();
        Codeable codeable = CodeableEnumUtils.getCodeableEnumByCode(columnValue, clazz);
        if (Objects.isNull(codeable)) {
            log.error(String.format("[scm-wip][sub-rule][rule-detail] {}错误，{} = {}。", columnDesc, columnName, columnValue));
            throw new ParamsIncorrectException("抱歉，系统出现了未知错误，速联系相关人员。");
        }
        setConsumer.accept(codeable.getDesc());
    }

    /**
     * 校验更新数据的正确性，返回错误信息，并将无误数据添加到参数 {@param updateData}
     */
    private String validateAndGetUpdateData(WipSubRuleEntity rule, List<WipSubRuleEntity> updateData) {
        String errorMessage = universalValidate(rule, ChangedTypeEnum.UPDATE);
        if (errorMessage.length() > 0) {
            return errorMessage;
        }
        if (StringUtils.isEmpty(rule.getRuleId())) {
            return "更新失败，待更新的数据中缺少规则ID，请仔细修改后再重试；";
        }
        List<WipSubRuleEntity> dbRuleList = wipSubRuleRepository.selectByRuleNo(rule.getRuleNo());
        if (CollectionUtil.isEmpty(dbRuleList)) {
            return "更新失败，规则编号错误，请仔细修改后再重试；";
        } else if (!dbRuleList.get(0).getRuleId().equals(rule.getRuleId())) {
            return "更新失败，规则编号与待更新的数据不一致，请仔细修改后再重试；";
        }
        updateData.add(rule);
        return "";
    }

}