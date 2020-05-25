package com.cvte.scm.wip.infrastructure.subrule.repository;

import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.deprecated.BaseBatchMapper;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleAdaptEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleAdaptRepository;
import com.cvte.scm.wip.domain.core.subrule.valueobject.EntryVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.WipSubRuleLotDetailVO;
import com.cvte.scm.wip.infrastructure.subrule.mapper.WipSubRuleAdaptMapper;
import com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject.WipSubRuleAdaptDO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 20:43
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipSubRuleAdaptRepositoryImpl implements WipSubRuleAdaptRepository {

    private BaseBatchMapper batchMapper;

    private WipSubRuleAdaptMapper adaptMapper;

    public WipSubRuleAdaptRepositoryImpl(@Qualifier("pgBatchMapper") BaseBatchMapper batchMapper, WipSubRuleAdaptMapper adaptMapper) {
        this.batchMapper = batchMapper;
        this.adaptMapper = adaptMapper;
    }

    @Override
    public void batchInsert(List<WipSubRuleAdaptEntity> adaptEntityList) {
        List<WipSubRuleAdaptDO> adaptDOList = WipSubRuleAdaptDO.batchBuildDO(adaptEntityList);
        batchMapper.insert(adaptDOList);
    }

    @Override
    public void deleteByIds(String... ids) {
        adaptMapper.deleteByIds(ids);
    }

    @Override
    public List<WipSubRuleAdaptEntity> getByRuleId(String ruleId) {
        List<WipSubRuleAdaptDO> adaptDOList = adaptMapper.selectByExample(EntityUtils.getExample(WipSubRuleAdaptDO.class, "ruleId", ruleId));
        return WipSubRuleAdaptDO.batchBuildEntity(adaptDOList);
    }

    @Override
    public List<WipSubRuleLotDetailVO> getSubRuleLotDetailData(String condition) {
        return adaptMapper.getSubRuleLotDetailData(condition);
    }

    @Override
    public List<String> getPreparedLotNos(String organizationId, Collection<String> lotNoList) {
        return adaptMapper.getPreparedLotNos(organizationId, lotNoList);
    }

    @Override
    public Set<EntryVO> getFactoryNameData(Collection<String> factoryIds) {
        return adaptMapper.getFactoryNameData(factoryIds);
    }
}
