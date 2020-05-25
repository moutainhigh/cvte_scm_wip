package com.cvte.scm.wip.domain.core.subrule.repository;

import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleItemEntity;
import com.cvte.scm.wip.domain.core.subrule.valueobject.WipSubRuleItemDetailVO;

import java.util.Collection;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 20:09
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipSubRuleItemRepository {

    void batchInsert(List<WipSubRuleItemEntity> entityList);

    void deleteByIds(String... ids);

    List<WipSubRuleItemEntity> getByRuleId(String ruleId);

    List<WipSubRuleItemEntity> getSubItemNoAggregateData(String ruleId, String splitSymbol);

    List<WipSubRuleItemDetailVO> getSubItemDetail(String organizationId, Collection<String> itemNos);

    List<String> getRepeatSubItemRuleIds(String ruleId, String organizationId, String itemCondition, Collection<Object> ruleStatusCollection);

}
