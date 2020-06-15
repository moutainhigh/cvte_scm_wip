package com.cvte.scm.wip.infrastructure.subrule.repository;

import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.infrastructure.deprecated.BaseBatchMapper;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleItemEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleItemRepository;
import com.cvte.scm.wip.domain.core.subrule.valueobject.SubItemValidateVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.WipSubRuleItemDetailVO;
import com.cvte.scm.wip.infrastructure.subrule.mapper.WipSubRuleItemMapper;
import com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject.WipSubRuleItemDO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 20:14
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipSubRuleItemRepositoryImpl implements WipSubRuleItemRepository {

    private BaseBatchMapper batchMapper;
    private WipSubRuleItemMapper ruleItemMapper;

    public WipSubRuleItemRepositoryImpl(@Qualifier("pgBatchMapper") BaseBatchMapper batchMapper, WipSubRuleItemMapper ruleItemMapper) {
        this.batchMapper = batchMapper;
        this.ruleItemMapper = ruleItemMapper;
    }

    @Override
    public void batchInsert(List<WipSubRuleItemEntity> entityList) {
        List<WipSubRuleItemDO> ruleItemDOList = WipSubRuleItemDO.batchBuildDO(entityList);
        batchMapper.insert(ruleItemDOList);
    }

    @Override
    public void deleteByIds(String... ids) {
        ruleItemMapper.deleteByIds(ids);
    }

    @Override
    public List<WipSubRuleItemEntity> getByRuleId(String ruleId) {
        Example example = EntityUtils.getExample(WipSubRuleItemDO.class, "ruleId", ruleId);
        List<WipSubRuleItemDO> itemDOList = ruleItemMapper.selectByExample(example);
        return WipSubRuleItemDO.batchBuildEntity(itemDOList);
    }

    @Override
    public List<WipSubRuleItemEntity> getSubItemNoAggregateData(String ruleId, String splitSymbol) {
        List<WipSubRuleItemDO> ruleItemDOList = ruleItemMapper.getSubItemNoAggregateData(ruleId, splitSymbol);
        return WipSubRuleItemDO.batchBuildEntity(ruleItemDOList);
    }

    @Override
    public List<WipSubRuleItemDetailVO> getSubItemDetail(String organizationId, Collection<String> itemNos) {
        return ruleItemMapper.getSubItemDetail(organizationId, itemNos);
    }

    @Override
    public List<SubItemValidateVO> getRepeatSubItemRuleIds(String ruleId, String organizationId, List<SubItemValidateVO> subItemNoList, List<String> scopeValueList, Collection<Object> ruleStatusCollection) {
        return ruleItemMapper.getRepeatSubItemRuleIds(ruleId, organizationId, subItemNoList, scopeValueList, ruleStatusCollection);
    }

}
