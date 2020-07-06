package com.cvte.scm.wip.domain.core.subrule.service;

import cn.hutool.core.collection.CollUtil;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.BooleanEnum;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.common.utils.LambdaUtils;
import com.cvte.scm.wip.domain.common.deprecated.LazyExecution;
import com.cvte.scm.wip.domain.core.item.service.ScmItemService;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleEntity;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleItemEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleItemRepository;
import com.cvte.scm.wip.domain.core.subrule.valueobject.GroupObjectVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.SubItemValidateVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.WipSubRuleItemDetailVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleScopeTypeEnum;
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleStatusEnum;
import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.cvte.csb.toolkit.StringUtils.isEmpty;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 20:24
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipSubRuleItemService {

    private static final String SPLIT_SYMBOL = ";";

    private ScmItemService scmItemService;

    private WipSubRuleItemRepository wipSubRuleItemRepository;

    public WipSubRuleItemService(ScmItemService scmItemService,  WipSubRuleItemRepository wipSubRuleItemRepository) {
        this.scmItemService = scmItemService;
        this.wipSubRuleItemRepository = wipSubRuleItemRepository;
    }

    public Object insertOrDelete(WipSubRuleEntity wipSubRuleEntity) {

        String ruleId = wipSubRuleEntity.getRuleId(), organizationId = wipSubRuleEntity.getOrganizationId();
        List<String[]> subItemNoList = wipSubRuleEntity.getSubItemNoList();
        if (isEmpty(ruleId) || isEmpty(organizationId) || CollUtil.isEmpty(subItemNoList)) {
            return "替换物料信息为空";
        }
        subItemNoList = convertIntoOneToOne(subItemNoList);
        // 获取替换前后所有物料编号信息，用于校验物料编码的正确性。
        Set<String> itemNoList = subItemNoList.stream().map(Arrays::asList).flatMap(List::stream).collect(toSet());
        Map<String, String> itemMap = scmItemService.getItemMap(organizationId, itemNoList);
        if (itemMap.isEmpty() || itemMap.size() != itemNoList.size()) {
            return "替换前后物料编码存在错误";
        }
        /* 成套替代时，不同组合可能存在相同替代前后的物料组合，故成套替代字段为 "是" 时，不校验替换物料的重复性。 */
        if (CodeableEnumUtils.getCodeableEnumByCode(wipSubRuleEntity.getIfCouple(), BooleanEnum.class) == BooleanEnum.NO) {
            String errMsg = validateDuplicateItem(subItemNoList, wipSubRuleEntity.getScopeMap(), ruleId, organizationId);
            if (StringUtils.isNotBlank(errMsg)) {
                return errMsg;
            }
        }
        // 校验物料是否在批次对应的投料单中, 有不存在的则报错
        String errMsg = validateItemReq(subItemNoList, wipSubRuleEntity.getScopeMap(), organizationId);
        if (StringUtils.isNotBlank(errMsg)) {
            return errMsg;
        }
        List<WipSubRuleItemEntity> insertionData = new LinkedList<>();
        List<String> deletionData = new LinkedList<>();
        markAdaptRule(subItemNoList, itemMap, ruleId, deletionData, insertionData);
        return (LazyExecution) () -> {
            if (ListUtil.notEmpty(deletionData)) {
                wipSubRuleItemRepository.deleteByIds(deletionData.toArray(new String[0]));
            }
            wipSubRuleItemRepository.batchInsert(insertionData);
        };
    }

    public List<String[]> getSubRuleItemData(String ruleId) {
        if (StringUtils.isEmpty(ruleId)) {
            return Collections.emptyList();
        }
        return wipSubRuleItemRepository.getSubItemNoAggregateData(ruleId, SPLIT_SYMBOL).stream()
                .map(item -> new String[]{item.getRmk01(), item.getRmk02()}).collect(toList());
    }

    /**
     * 将替换前后 "多对多" 的物料信息转换为 "一对一"。
     */
    private List<String[]> convertIntoOneToOne(List<String[]> subItemNoList) {
        Predicate<String[]> isValid = subItemNo -> {
            if (isNull(subItemNo) || subItemNo.length != 2 || StringUtils.isEmpty(subItemNo)) {
                throw new ParamsIncorrectException("糟糕，系统出现了未知错误，速联系相关人员。");
            }
            return true;
        };
        // 将替换前后的物料编码行分割，执行笛卡尔积操作，并将所有替换信息添加到列表。
        // ["1;2","3;4"] --> [["1","3"],["1","4"],["2","3"],["2","4"]]
        Function<String, Supplier<Stream<String>>> suppliers = s -> () -> stream(s.split(SPLIT_SYMBOL));
        return subItemNoList.stream().filter(isValid).map(sub -> LambdaUtils.cartesian((a, b) -> a.concat(SPLIT_SYMBOL).concat(b),
                suppliers.apply(sub[0]), suppliers.apply(sub[1])).map(s -> s.split(SPLIT_SYMBOL)).collect(toList()))
                .flatMap(List::stream).collect(toList());
    }

    /**
     * 根据规则ID获取物料规则的映射表对象，key 为更改前后物料ID的组合对象{@link GroupObjectVO}，value 为物料规则表主键。
     */
    private Map<GroupObjectVO, String> getItemRuleMap(String ruleId) {
        Function<WipSubRuleItemEntity, GroupObjectVO> keyMapper = item -> new GroupObjectVO(new String[]{item.getBeforeItemId(), item.getAfterItemId()});
        return wipSubRuleItemRepository.getByRuleId(ruleId).stream().collect(Collectors.toMap(keyMapper, WipSubRuleItemEntity::getItemRuleId));
    }

    /**
     * 校验替换前后的物料信息，返回错误信息，并将正确的数据插入待添加的数据集 {@param insertionData} 中。
     * <p>
     * 替换前后的物料信息不能与数据库已有的重复，也不能与规则已存在的信息冲突。
     */
    private void markAdaptRule(List<String[]> subItemNosList, Map<String, String> itemMap, String ruleId,
                               List<String> deletionData, List<WipSubRuleItemEntity> insertionData) {
        Map<GroupObjectVO, String> itemRuleMap = getItemRuleMap(ruleId);
        for (String[] subItemNos : subItemNosList) {
            GroupObjectVO groupObjects = new GroupObjectVO(new String[2]);
            groupObjects.getGroupObjects()[0] = itemMap.get(subItemNos[0]);
            groupObjects.getGroupObjects()[1] = itemMap.get(subItemNos[1]);
            if (itemRuleMap.containsKey(groupObjects)) {
                itemRuleMap.put(groupObjects, null);
            } else {
                insertionData.add(createWipSubRuleItem((String[]) groupObjects.getGroupObjects(), subItemNos, ruleId));
            }
        }
        itemRuleMap.values().stream().filter(Objects::nonNull).forEach(deletionData::add);
    }

    /**
     * 校验替换物料的重复性，只校验状态为 审核中、生效中 的规则。
     */
    private String validateDuplicateItem(List<String[]> subItemNosList, Map<String, Map<String, String>> scopeMap, String ruleId, String organizationId) {
        List<String> scopeValueList = new ArrayList<>();
        for (Map<String, String> scope : scopeMap.values()) {
            for (String dimValue : scope.values()) {
                scopeValueList.addAll(Arrays.asList(dimValue.split(";")));
            }
        }

        List<SubItemValidateVO> subItemValidateDTOList = new ArrayList<>();
        for (String[] subItemNo : subItemNosList) {
            SubItemValidateVO subItemValidateDTO = new SubItemValidateVO().setBeforeItemNo(subItemNo[0]).setAfterItemNo(subItemNo[1]);
            subItemValidateDTOList.add(subItemValidateDTO);
        }

        final int REVIEW_EFFECTIVE = 6;
        List<Object> ruleStatusList = CodeableEnumUtils.getCodesByOrdinalFlag(REVIEW_EFFECTIVE, SubRuleStatusEnum.class);
        List<SubItemValidateVO> itemValidateList = wipSubRuleItemRepository.getRepeatSubItemRuleIds(ruleId, organizationId, subItemValidateDTOList, scopeValueList, ruleStatusList);

        if (ListUtil.notEmpty(itemValidateList)) {
            String scopeTypeName = CodeableEnumUtils.getCodeableEnumByCode(scopeMap.keySet().stream().distinct().collect(Collectors.toList()).get(0), SubRuleScopeTypeEnum.class).getDesc();
            StringBuilder errMsgBuilder = new StringBuilder();
            for (SubItemValidateVO validateVO : itemValidateList) {
                errMsgBuilder.append(String.format("【%s%s已存在 有效规则%s】", scopeTypeName, validateVO.getAdaptItem(), validateVO.getRuleNo()));
            }
            return errMsgBuilder.toString();
        }
        return "";
    }

    /**
     * 校验物料是否在批次对应的投料单中
     * @since 2020/6/9 9:13 上午
     * @author xueyuting
     */
    @VisibleForTesting
    protected String validateItemReq(List<String[]> subItemNosList, Map<String, Map<String, String>> scopeMap, String organizationId) {
        for (Map.Entry<String, Map<String, String>> scopeEntry : scopeMap.entrySet()) {
            String scope = scopeEntry.getKey();
            if (SubRuleScopeTypeEnum.PRODUCTION_LOT.getCode().equals(scope)) {
                // 拿出所有批次
                List<String> lotNoList = new ArrayList<>();
                scopeEntry.getValue().values().stream().distinct().forEach(scopeValue -> lotNoList.addAll(Arrays.asList(scopeValue.split(";"))));
                // 拿出所有替换前物料
                List<String> beforeItemNoList = subItemNosList.stream().map(nos -> nos[0]).collect(toList());
                // 获取有效(替换前物料在批次对应的投料单中)的列表
                List<SubItemValidateVO> validItemList = wipSubRuleItemRepository.getItemsInReq(lotNoList, beforeItemNoList, organizationId);

                List<SubItemValidateVO> originItemList = new ArrayList<>();
                lotNoList.forEach(lotNo -> beforeItemNoList.forEach(itemNo -> originItemList.add(new SubItemValidateVO().setAdaptItem(lotNo).setBeforeItemNo(itemNo))));
                // 原始和有效取差集, 结果不为空则报错
                List<SubItemValidateVO> subtractResult = (List<SubItemValidateVO>)CollectionUtils.subtract(originItemList, validItemList);
                if (ListUtil.notEmpty(subtractResult)) {
                    StringBuilder errMsgBuilder = new StringBuilder();
                    subtractResult.forEach(vo -> errMsgBuilder.append(String.format("【%s批次中不存在物料%s】", vo.getAdaptItem(), vo.getBeforeItemNo())));
                    return errMsgBuilder.toString();
                }
            }
        }
        return "";
    }

    /**
     * 根据替换物料信息创建替换物料规则对象 {@link WipSubRuleItemEntity} 。
     *
     * @param subItemIds 替换物料 ID 数组，索引 0 为替换前物料 ID，索引 1 为替换后物料 ID。
     * @param subItemNos 替换物料编号数组，索引 0 为替换前物料编号，索引 1 为替换后物料编号。
     * @param ruleId     规则 ID
     */
    private WipSubRuleItemEntity createWipSubRuleItem(String[] subItemIds, String[] subItemNos, String ruleId) {
        WipSubRuleItemEntity wipSubRuleItem = new WipSubRuleItemEntity().setItemRuleId(UUIDUtils.getUUID()).setRuleId(ruleId)
                .setBeforeItemId(subItemIds[0]).setAfterItemId(subItemIds[1]).setRmk01(subItemNos[0]).setRmk02(subItemNos[1]);
        EntityUtils.writeStdCrtInfoToEntity(wipSubRuleItem, EntityUtils.getWipUserId());
        return wipSubRuleItem;
    }

    private static final UnaryOperator<String> ERROR_FORMAT = e -> String.format("查看失败，%s，请仔细修改后再重试。", e);

    public Map<String, List<WipSubRuleItemDetailVO>> getSubItemDetail(String organizationId, String[] subItemNos) {
        if (StringUtils.isEmpty(organizationId)) {
            throw new ParamsIncorrectException(ERROR_FORMAT.apply("组织信息为空"));
        } else if (subItemNos == null || subItemNos.length != 2 || StringUtils.isEmpty(subItemNos)) {
            throw new ParamsIncorrectException(ERROR_FORMAT.apply("糟糕，系统出现了未知错误，速联系相关人员。"));
        }
        /* 获取替换前后的所有物料编码 */
        Set<String> itemNoSet = stream(subItemNos).map(s -> asList(s.split(SPLIT_SYMBOL))).flatMap(List::stream).collect(toSet());
        /* 根据物料编码和组织，获取物料详情 */
        Map<String, WipSubRuleItemDetailVO> itemDetailMap = wipSubRuleItemRepository.getSubItemDetail(organizationId, itemNoSet)
                .stream().collect(Collectors.toMap(WipSubRuleItemDetailVO::getItemNo, e -> e));
        Map<String, List<WipSubRuleItemDetailVO>> subItemDetail = new HashMap<>(4);
        subItemDetail.put("before", stream(subItemNos[0].split(SPLIT_SYMBOL)).map(itemDetailMap::get).collect(toList()));
        subItemDetail.put("after", stream(subItemNos[1].split(SPLIT_SYMBOL)).map(itemDetailMap::get).collect(toList()));
        return subItemDetail;
    }
    
    public Map<String, String> validateSubItem(List<SubItemValidateVO> itemValidateDTOList) {
        if (Objects.isNull(itemValidateDTOList)) {
            return null;
        }
        List<String> organizationIdList = itemValidateDTOList.stream().map(SubItemValidateVO::getOrganizationId).distinct().collect(toList());
        if (ListUtil.empty(organizationIdList) || organizationIdList.size() > 1) {
            throw new ParamsIncorrectException("必须只能有一个组织ID");
        }
        List<String> itemNoList = new ArrayList<>();
        for (SubItemValidateVO subItemValidateDTO : itemValidateDTOList) {
            if (StringUtils.isNotBlank(subItemValidateDTO.getBeforeItemNo())) {
                itemNoList.add(subItemValidateDTO.getBeforeItemNo());
            }
            if (StringUtils.isNotBlank(subItemValidateDTO.getAfterItemNo())) {
                itemNoList.add(subItemValidateDTO.getAfterItemNo());
            }
        }
        // 去重
        itemNoList = itemNoList.stream().distinct().collect(toList());
        Map<String, String> itemMap = scmItemService.getItemMap(organizationIdList.get(0), itemNoList);
        Set<String> existsItemNoList = itemMap.keySet();
        if (existsItemNoList.size() != itemNoList.size()) {
            StringBuilder errMsgBuilder = new StringBuilder();
            for (String itemNo : itemNoList) {
                if (!existsItemNoList.contains(itemNo)) {
                    errMsgBuilder.append("[").append(itemNo).append("]");
                }
            }
            errMsgBuilder.append("无效的物料编码");
            throw new ParamsIncorrectException(errMsgBuilder.toString());
        }
        return itemMap;
    }

}
