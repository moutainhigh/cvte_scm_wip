package com.cvte.scm.wip.infrastructure.subrule.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.core.subrule.valueobject.SubItemValidateVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.WipSubRuleItemDetailVO;
import com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject.WipSubRuleItemDO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @author jf
 * @since 2020-02-17
 */
public interface WipSubRuleItemMapper extends CommonMapper<WipSubRuleItemDO> {

    /* 根据指定 规则ID 查询替换物料信息，并将利用替换后的物料聚集，替换前的物料用 分隔符 分隔。 */
    List<WipSubRuleItemDO> getSubItemNoAggregateData(@Param("ruleId") String ruleId, @Param("splitSymbol") String splitSymbol);

    /* 获取指定组织ID下的物料明细 */
    List<WipSubRuleItemDetailVO> getSubItemDetail(@Param("organizationId") String organizationId, @Param("itemNos") Collection<String> itemNos);

    /* 获取替换前后物料信息重复的规则ID */
    List<SubItemValidateVO> getRepeatSubItemRuleIds(@Param("ruleId") String ruleId,
                                         @Param("organizationId") String organizationId,
                                         @Param("subItemNoList") List<SubItemValidateVO> subItemNoList,
                                         @Param("scopeValueList") List<String> scopeValueList,
                                         @Param("ruleStatusCollection") Collection<Object> ruleStatusCollection);
}