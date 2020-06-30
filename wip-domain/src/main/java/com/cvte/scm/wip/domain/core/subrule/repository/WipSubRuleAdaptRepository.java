package com.cvte.scm.wip.domain.core.subrule.repository;

import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleAdaptEntity;
import com.cvte.scm.wip.domain.core.subrule.valueobject.EntryVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.WipSubRuleLotDetailVO;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 20:41
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface WipSubRuleAdaptRepository {

    void batchInsert(List<WipSubRuleAdaptEntity> adaptEntityList);

    void deleteByIds(String... ids);

    List<WipSubRuleAdaptEntity> getByRuleId(String ruleId);

    List<WipSubRuleLotDetailVO> getSubRuleLotDetailData(String condition);

    List<String> getPreparedLotNos(String organizationId, Collection<String> lotNoList);

    Set<EntryVO> getFactoryNameData(Collection<String> factoryIds);

}
