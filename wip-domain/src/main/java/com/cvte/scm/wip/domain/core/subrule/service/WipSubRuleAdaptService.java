package com.cvte.scm.wip.domain.core.subrule.service;

import cn.hutool.core.collection.CollUtil;
import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.deprecated.LazyExecution;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleAdaptEntity;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleAdaptRepository;
import com.cvte.scm.wip.domain.core.subrule.valueobject.EntryVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.GroupObjectVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.WipSubRuleLotDetailVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleAdaptTypeEnum;
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleScopeTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.*;

/**
 * @author jf
 * @since 2020-02-17
 */
@Slf4j
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipSubRuleAdaptService {

    public static final String SPLIT_SYMBOL = ";";

    private static final String EMPTY_STRING = "";

    private WipSubRuleAdaptRepository wipSubRuleAdaptRepository;

    public WipSubRuleAdaptService(WipSubRuleAdaptRepository wipSubRuleAdaptRepository) {
        this.wipSubRuleAdaptRepository = wipSubRuleAdaptRepository;
    }

    public Object insertOrDelete(WipSubRuleEntity wipSubRule) {
        String ruleId = wipSubRule.getRuleId(), organizationId = wipSubRule.getOrganizationId();
        Map<String, Map<String, String>> scopeMap = wipSubRule.getScopeMap();
        if (StringUtils.isEmpty(ruleId) || StringUtils.isEmpty(organizationId) || CollUtil.isEmpty(scopeMap)) {
            return "适用信息为空";
        }
        Map<GroupObjectVO, String> adaptRuleMap = getAdaptRuleMap(ruleId);
        String errorMessage;
        if ((errorMessage = markAdaptRule(organizationId, adaptRuleMap, scopeMap)).length() > 0) {
            return errorMessage;
        }
        List<String> deletionList = new ArrayList<>();
        List<WipSubRuleAdaptEntity> insertionList = new ArrayList<>();
        assembleItemRule(adaptRuleMap, ruleId, deletionList, insertionList);
        return (LazyExecution) () -> {
            if (ListUtil.notEmpty(deletionList)) {
                wipSubRuleAdaptRepository.deleteByIds(deletionList.toArray(new String[0]));
            }
            wipSubRuleAdaptRepository.batchInsert(insertionList);
        };
    }

    /**
     * 根据规则 ID 获取指定的适用规则数据。
     * <p>
     * 其结果是根据适用范围以及匹配属性进行二次分组，并将聚合后的匹配对象利用 {@link WipSubRuleAdaptService#SPLIT_SYMBOL} 分隔。
     * 具体结果的JSON字符串例子：{BOM={equal=1, include=1;2;3}, production_lot={equal=SA-SY19091171}}
     */
    public Map<String, Map<String, String>> getAdaptRuleData(String ruleId) {
        if (StringUtils.isEmpty(ruleId)) {
            return Collections.emptyMap();
        }
        return wipSubRuleAdaptRepository.getByRuleId(ruleId).stream().collect(groupingBy(
                WipSubRuleAdaptEntity::getScopeType,
                groupingBy(
                        WipSubRuleAdaptEntity::getAdaptType,
                        collectingAndThen(toList(), e -> e.stream().map(WipSubRuleAdaptEntity::getAdaptItem).collect(joining(SPLIT_SYMBOL)))
                )
        ));
    }

    /**
     * 根据指定规则ID获取批次明细
     */
    public List<WipSubRuleLotDetailVO> getSubRuleLotDetailData(String organizationId, String ruleId) {
        if (StringUtils.isEmpty(ruleId)) {
            return Collections.emptyList();
        }
        Map<String, Collection<String>> scopeMap = wipSubRuleAdaptRepository.getByRuleId(ruleId)
                .stream().filter(adapt -> SubRuleAdaptTypeEnum.EQUAL.getCode().equalsIgnoreCase(adapt.getAdaptType()))
                .collect(groupingBy(
                        WipSubRuleAdaptEntity::getScopeType,
                        collectingAndThen(
                                Collectors.toList(),
                                e -> e.stream().map(WipSubRuleAdaptEntity::getAdaptItem).collect(Collectors.toList())
                        )));
        return getSubRuleLotDetailData(organizationId, scopeMap);
    }

    /**
     * 根据组织和批次号获取指定的生产批次明细。
     */
    public List<WipSubRuleLotDetailVO> getSubRuleLotDetailData(String organizationId, String scopeType, Collection<String> values) {
        return getSubRuleLotDetailData(organizationId, Collections.singletonMap(scopeType, values));
    }

    /**
     * 根据指定的组织以及适用信息获取批次明细。
     *
     * @param organizationId 组织ID
     * @param scopeMap       适用信息，key 为适用范围，value 为匹配对象列表
     * @return 批次明细列表
     */
    public List<WipSubRuleLotDetailVO> getSubRuleLotDetailData(String organizationId, Map<String, Collection<String>> scopeMap) {
        if (StringUtils.isEmpty(organizationId) || CollectionUtils.isEmpty(scopeMap)) {
            return Collections.emptyList();
        }
        /* 根据不同适用范围拼接对应的查询SQL */
        StringBuilder where = new StringBuilder("where organization_id = '").append(organizationId).append("' and (");
        int initLen = where.length();
        BiConsumer<String, String> in = (c, p) -> where.append(' ').append(c).append(" in(").append(p).append(')').append(" or");
        for (Map.Entry<String, Collection<String>> entry : scopeMap.entrySet()) {
            SubRuleScopeTypeEnum typeEnum = CodeableEnumUtils.getCodeableEnumByCode(entry.getKey(), SubRuleScopeTypeEnum.class);
            if (nonNull(typeEnum) && CollectionUtils.isNotEmpty(entry.getValue())) {
                String param = entry.getValue().stream().map(e -> "'".concat(e).concat("'")).collect(joining(","));
                String columnName;
                switch (typeEnum) {
                    case BOM:
                        columnName = "product_no";
                        break;
                    case PRODUCTION_LOT:
                        columnName = "source_lot_no";
                        break;
                    case DEVELOPMENT_MODEL:
                        columnName = "product_model";
                        break;
                    default:
                        continue;
                }
                in.accept(columnName, param);
            }
        }
        if (where.length() == initLen) {
            return Collections.emptyList();
        }
        where.setLength(where.length() - 2);               /* 消除最后的 or 字符串 */
        where.setCharAt(where.length() - 1, ')'); /* 末尾添加 ) 字符 */
        List<WipSubRuleLotDetailVO> detailData = wipSubRuleAdaptRepository.getSubRuleLotDetailData(where.toString());
        transferFactoryIdToName(detailData);
        return detailData;
    }

    /**
     * 将 {@link WipSubRuleLotDetailVO} 中的工厂 ID 转换为工厂名称
     */
    private void transferFactoryIdToName(List<WipSubRuleLotDetailVO> detailData) {
        List<String> factoryIds = detailData.stream().map(WipSubRuleLotDetailVO::getFactoryId).collect(Collectors.toList());
        Map<Object, Object> factoryIdNameMap = wipSubRuleAdaptRepository.getFactoryNameData(factoryIds).stream()
                .collect(toMap(EntryVO::getKey, EntryVO::getValue, (m, n) -> m));
        for (WipSubRuleLotDetailVO detail : detailData) {
            detail.setFactoryId((String) factoryIdNameMap.getOrDefault(detail.getFactoryId(), detail.getFactoryId()));
        }
    }

    /**
     * 根据规则ID {@param ruleId} 获取适用规则映射表类型。
     * <p>
     * 映射表key为适用范围、适用属性和适用对象的组合对象{@link GroupObjectVO}，value 为适用规则主键ID。
     * <p>
     * 与此同时，此方法也校验数据库中适用范围类型的正确性，必须保证一个规则只能有一个适用范围，且适用范围类型必须满足要求，参见{@link SubRuleScopeTypeEnum}。
     */
    private Map<GroupObjectVO, String> getAdaptRuleMap(String ruleId) {
        List<WipSubRuleAdaptEntity> wipSubRuleAdaptList = wipSubRuleAdaptRepository.getByRuleId(ruleId);
        if (wipSubRuleAdaptList.isEmpty()) {
            return new HashMap<>();
        }
        Map<GroupObjectVO, String> adaptRuleMap = new HashMap<>(wipSubRuleAdaptList.size() * 4 / 3 + 1);
        for (WipSubRuleAdaptEntity adapt : wipSubRuleAdaptList) {
            String scopeType = adapt.getScopeType(), adaptType = adapt.getAdaptType();
            if (CodeableEnumUtils.inValid(scopeType, SubRuleScopeTypeEnum.class) || CodeableEnumUtils.inValid(adaptType, SubRuleAdaptTypeEnum.class)) {
                log.error("[scm-wip][rule-adapt] 出现了错误的适用范围或者匹配属性：adapt_rule_id = {}；", adapt.getAdaptRuleId());
                throw new ParamsIncorrectException("糟糕，系统出现了未知数据，速联系相关人员；");
            }
            GroupObjectVO groupObjects = new GroupObjectVO(new String[]{scopeType, adaptType, adapt.getAdaptItem()});
            adaptRuleMap.put(groupObjects, adapt.getAdaptRuleId());
        }
        return adaptRuleMap;
    }


    /**
     * 标记物料规则，通过修改参数 {@param adaptRuleMap} 的 value 值，达到标记的效果
     * 如果数据表已经存在该规则(适用属性和适用对象组合)，则将该规则设置为"可忽略"，即 value 设为 null；
     * 如果数据表不存在该规则，则将该规则设置为"可新增"，即 value 设为空字符串 {@link WipSubRuleAdaptService#EMPTY_STRING}；
     * 如果数据库的规则不存在于此次规则组合，则将该规则设置为"可删除"，即继续保持 value 为物料规则表主键。
     * <p>
     * 校验数据库的适用属性是否正确
     */
    @SuppressWarnings("ConstantConditions")
    private String markAdaptRule(String organizationId, Map<GroupObjectVO, String> adaptRuleMap, Map<String, Map<String, String>> scopeMap) {
        String errorMessage;
        for (Map.Entry<String, Map<String, String>> entry : scopeMap.entrySet()) {
            SubRuleScopeTypeEnum scopeTypeEnum = CodeableEnumUtils.getCodeableEnumByCode(entry.getKey(), SubRuleScopeTypeEnum.class);
            if ((errorMessage = validateScopeTypeData(scopeTypeEnum, organizationId, entry.getValue())).length() > 0) {
                return errorMessage;
            }
            for (Map.Entry<String, String> adapt : entry.getValue().entrySet()) {
                String adaptType;
                if (CodeableEnumUtils.inValid(adaptType = adapt.getKey(), SubRuleAdaptTypeEnum.class)) {
                    return "适用类型错误";
                } else if (StringUtils.isEmpty(adapt.getValue())) {
                    return "适用对象为空";
                }
                for (String adaptItem : adapt.getValue().split(SPLIT_SYMBOL)) {
                    GroupObjectVO groupObjects = new GroupObjectVO(new String[]{scopeTypeEnum.getCode(), adaptType, adaptItem});
                    adaptRuleMap.put(groupObjects, adaptRuleMap.containsKey(groupObjects) ? null : EMPTY_STRING);
                }
            }
        }
        return "";
    }

    /**
     * 校验适应范围类型，以及特定的匹配信息。
     */
    private String validateScopeTypeData(SubRuleScopeTypeEnum type, String organizationId, Map<String, String> adaptMap) {
        if (type == null) {
            return "适用范围错误";
        } else if (CollUtil.isEmpty(adaptMap)) {
            return "匹配信息为空";
        } else if (type == SubRuleScopeTypeEnum.ORDER_CUSTOMER) {
            return "";
        }
        /* 校验批次号、产品编号以及产品型号的正确性 */
        Set<String> adaptItems = adaptMap.values().stream().map(s -> s.split(SPLIT_SYMBOL)).flatMap(Arrays::stream).collect(toSet());
        Stream<WipSubRuleLotDetailVO> detailStream = getSubRuleLotDetailData(organizationId, type.getCode(), adaptItems).stream();
        Stream<String> validStream;
        switch (type) {
            case DEVELOPMENT_MODEL:
                validStream = detailStream.map(WipSubRuleLotDetailVO::getProductModel).filter(adaptItems::contains).distinct();
                break;
            case PRODUCTION_LOT:
                validStream = detailStream.map(WipSubRuleLotDetailVO::getSourceLotNo).filter(adaptItems::contains).distinct();
                break;
            case BOM:
                validStream = detailStream.map(WipSubRuleLotDetailVO::getProductNo).filter(adaptItems::contains).distinct();
                break;
            default:
                return "";
        }
        List<String> validAdaptItems = validStream.collect(toList());
        if (validAdaptItems.size() != adaptItems.size()) {
            adaptItems.removeAll(validAdaptItems);
            return type.getDesc() + adaptItems + "不存在";
        }
        if (type == SubRuleScopeTypeEnum.PRODUCTION_LOT) {
            List<String> issuedLots = wipSubRuleAdaptRepository.getPreparedLotNos(organizationId, validAdaptItems);
            if (ListUtil.notEmpty(issuedLots)) {
                return String.format("批次号【%s】存在领料单", String.join(",", issuedLots));
            }
        }
        return "";
    }

    /**
     * 根据前面的标记结果，将适用规则装配到对应的删除列表 {@param deletionList} 和 新增列表{@param insertionList}。
     * 删除列表仅仅存储待删除适用规则的主键ID。新增列表存储需要被新增到适用规则表的对象 {@link WipSubRuleAdaptEntity}。
     */
    private void assembleItemRule(Map<GroupObjectVO, String> adaptRuleMap, String ruleId,
                                  List<String> deletionList, List<WipSubRuleAdaptEntity> insertionList) {
        for (Map.Entry<GroupObjectVO, String> entry : adaptRuleMap.entrySet()) {
            String value = entry.getValue();
            if (nonNull(value)) { // 标记为null，表示可忽略。
                if (value.length() > 0) { // 标记为主键ID字符串，表示可删除。
                    deletionList.add(value);
                    continue;
                }
                // 标记为空字符串，表示可新增。
                String[] adaptInfos = (String[]) entry.getKey().getGroupObjects();
                WipSubRuleAdaptEntity wipSubRuleAdapt = new WipSubRuleAdaptEntity().setAdaptRuleId(UUIDUtils.getUUID())
                        .setRuleId(ruleId).setScopeType(adaptInfos[0]).setAdaptType(adaptInfos[1]).setAdaptItem(adaptInfos[2]);
                EntityUtils.writeStdCrtInfoToEntity(wipSubRuleAdapt, CurrentContextUtils.getOrDefaultUserId("SCM-WIP"));
                insertionList.add(wipSubRuleAdapt);
            }
        }
    }
}