package com.cvte.scm.wip.domain.core.subrule.service;

import cn.hutool.core.collection.CollUtil;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.deprecated.LazyExecution;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleEntity;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleWfEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleWfRepository;
import com.cvte.scm.wip.domain.core.subrule.valueobject.GroupObjectVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.ReviewerVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.*;

/**
 * @author jf
 * @since 2020-02-24
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipSubRuleWfService {

    private static final String EMPTY_STRING = "";

    private WipSubRuleWfRepository wipSubRuleWfRepository;

    public WipSubRuleWfService(WipSubRuleWfRepository wipSubRuleWfRepository) {
        this.wipSubRuleWfRepository = wipSubRuleWfRepository;
    }

    public Object insertOrDelete(WipSubRuleEntity wipSubRule) {
        String ruleId = wipSubRule.getRuleId();
        Map<String, List<ReviewerVO>> reviewerMap = wipSubRule.getReviewerMap();
        if (StringUtils.isEmpty(ruleId) || CollUtil.isEmpty(reviewerMap)) {
            return "审核人信息为空";
        }
        List<GroupObjectVO> reviewerInfoList = toReviewerInfoList(reviewerMap);
        Map<GroupObjectVO, String> reviewerInfoMap = getReviewerInfoMap(ruleId);
        markReviewerInfo(reviewerInfoMap, reviewerInfoList);
        List<String> deletionList = new ArrayList<>();
        List<WipSubRuleWfEntity> insertionList = new ArrayList<>();
        assembleReviewerInfo(reviewerInfoMap, ruleId, deletionList, insertionList);
        return (LazyExecution) () -> {
            if (ListUtil.notEmpty(deletionList)) {
                wipSubRuleWfRepository.deleteByIds(deletionList.toArray(new String[0]));
            }
            wipSubRuleWfRepository.batchInsert(insertionList);
        };
    }

    /**
     * 转换审核信息转换为审核组合对象列表，审核组合对象包括：审核人职位、审核人 ID 以及审核人名称。
     *
     * @param reviewerMap 审核信息，key 值为审核人职位，value 为 {@link ReviewerVO}对象列表，userId 为审核人ID，userName 为审核人名称。
     */
    private List<GroupObjectVO> toReviewerInfoList(Map<String, List<ReviewerVO>> reviewerMap) {
        BiFunction<String, ReviewerVO, GroupObjectVO> mapper = (s, r) -> new GroupObjectVO(new String[]{s, r.getUserId(), r.getUserName()});
        return reviewerMap.entrySet().stream().map(e -> e.getValue().stream().map(r -> mapper.apply(e.getKey(), r)).collect(toList()))
                .flatMap(List::stream).collect(Collectors.toList());
    }

    /**
     * 根据 {@param ruleId} 查询并转换为审核信息映射对象，key 值为审核人职位、审核人 ID 以及审核人名称的组合对象，value 为审核表主键值。
     */
    private Map<GroupObjectVO, String> getReviewerInfoMap(String ruleId) {
        Function<WipSubRuleWfEntity, GroupObjectVO> keySupplier = wf -> new GroupObjectVO(new String[]{wf.getNode(), wf.getUserId(), wf.getUserName()});
        return wipSubRuleWfRepository.selectByRuleId(ruleId)
                .stream().collect(HashMap::new, (map, wf) -> map.put(keySupplier.apply(wf), wf.getId()), HashMap::putAll);
    }

    /**
     * 标记审核人信息，通过修改参数 {@param reviewerInfoMap} 的 value 值，达到标记的效果
     * 如果审核表已经存在该审核(审核人职位、ID和名称)，则将该审核信息设置为"可忽略"，即 value 设为 null；
     * 如果审核表不存在该审核，则将该审核信息设置为"可新增"，即 value 设为空字符串；
     * 如果审核表的审核信息不存在于此次的审核信息列表中，则将该规则设置为"可删除"，即继续保持 value 为审核表主键。
     */
    private void markReviewerInfo(Map<GroupObjectVO, String> reviewerInfoMap, List<GroupObjectVO> reviewerInfoList) {
        for (GroupObjectVO reviewerInfo : reviewerInfoList) {
            reviewerInfoMap.put(reviewerInfo, reviewerInfoMap.containsKey(reviewerInfo) ? null : EMPTY_STRING);
        }
    }

    /**
     * 根据前面的标记结果，将审核信息装配到对应的删除列表 {@param deletionList} 和 新增列表{@param insertionList}。
     * 删除列表仅仅存储待删除审核主键ID。新增列表存储需要被新增到审核表的对象 {@link WipSubRuleWfEntity}。
     */
    private void assembleReviewerInfo(Map<GroupObjectVO, String> reviewerInfoMap, String ruleId,
                                      List<String> deletionList, List<WipSubRuleWfEntity> insertionList) {
        for (Map.Entry<GroupObjectVO, String> entry : reviewerInfoMap.entrySet()) {
            String value = entry.getValue();
            if (nonNull(value)) { // 标记为null，表示可忽略。
                if (value.length() > 0) { // 标记为主键ID字符串，表示可删除。
                    deletionList.add(value);
                    continue;
                }
                // 标记为空字符串，表示可新增。
                String[] reviewerInfos = (String[]) entry.getKey().getGroupObjects();
                WipSubRuleWfEntity wipSubRuleWf = new WipSubRuleWfEntity().setId(UUIDUtils.getUUID()).setRuleId(ruleId)
                        .setNode(reviewerInfos[0]).setUserId(reviewerInfos[1]).setUserName(reviewerInfos[2]);
                EntityUtils.writeStdCrtInfoToEntity(wipSubRuleWf, EntityUtils.getWipUserId());
                insertionList.add(wipSubRuleWf);
            }
        }
    }

    /**
     * 根据 {@param ruleId} 获取指定的审核信息，并转换为指定格式的映射对象，key 值为审核人职位，value 为审核人ID和审核人名称的组合对象列表。
     */
    public Map<String, List<ReviewerVO>> getReviewerData(String ruleId) {
        if (StringUtils.isEmpty(ruleId)) {
            return Collections.emptyMap();
        }
        Function<WipSubRuleWfEntity, ReviewerVO> reviewerMapper = wf -> new ReviewerVO(wf.getUserId(), wf.getUserName());
        // List<WipSubRuleWfEntity> -> Map<node, List<WipSubRuleWfEntity>> -> Map<node, List<ReviewerVO>>
        return wipSubRuleWfRepository.selectByRuleId(ruleId).stream()
                .collect(groupingBy(WipSubRuleWfEntity::getNode)).entrySet().stream()
                .collect(toMap(Map.Entry::getKey, e -> e.getValue().stream().map(reviewerMapper).collect(toList())));
    }
}