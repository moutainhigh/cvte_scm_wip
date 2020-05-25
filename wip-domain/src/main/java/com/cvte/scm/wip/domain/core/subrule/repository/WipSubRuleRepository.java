package com.cvte.scm.wip.domain.core.subrule.repository;

import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 21:09
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipSubRuleRepository {

    void insert(WipSubRuleEntity entity);

    Integer update(WipSubRuleEntity ruleEntity);

    void updateByRuleId(WipSubRuleEntity entity, List<String> ruleIdList);

    Integer expire();

    List<WipSubRuleEntity> selectList(WipSubRuleEntity ruleEntity);

    WipSubRuleEntity selectByRuleId(String ruleId);

    List<WipSubRuleEntity> selectByRuleNo(String ruleNo);

    WipSubRuleEntity selectByRuleIdAndStatus(String ruleId, String status);

    List<WipSubRuleEntity> selectByRuleIdAndStatus(List<String> ruleIdList, List<Object> statusList);

}
