package com.cvte.scm.wip.domain.core.subrule.repository;

import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleWfEntity;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 20:46
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipSubRuleWfRepository {

    List<WipSubRuleWfEntity> selectByRuleId(String ruleId);

    void insert(WipSubRuleWfEntity entity);

    void deleteByIdAndNode(String id, String node);

    void batchInsert(List<WipSubRuleWfEntity> entityList);

    void deleteByIds(String[] ids);

}
