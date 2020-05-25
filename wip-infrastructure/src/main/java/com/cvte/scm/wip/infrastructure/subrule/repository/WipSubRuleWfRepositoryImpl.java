package com.cvte.scm.wip.infrastructure.subrule.repository;

import com.cvte.scm.wip.common.enums.AuditorNodeEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.deprecated.BaseBatchMapper;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleWfEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleWfRepository;
import com.cvte.scm.wip.infrastructure.subrule.mapper.WipSubRuleWfMapper;
import com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject.WipSubRuleWfDO;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 20:50
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipSubRuleWfRepositoryImpl implements WipSubRuleWfRepository {

    private BaseBatchMapper batchMapper;
    private WipSubRuleWfMapper wipSubRuleWfMapper;

    public WipSubRuleWfRepositoryImpl(@Qualifier("pgBatchMapper") BaseBatchMapper batchMapper, WipSubRuleWfMapper wipSubRuleWfMapper) {
        this.batchMapper = batchMapper;
        this.wipSubRuleWfMapper = wipSubRuleWfMapper;
    }

    @Override
    public List<WipSubRuleWfEntity> selectByRuleId(String ruleId) {
        List<WipSubRuleWfDO> doList = wipSubRuleWfMapper.selectByExample(EntityUtils.getExample(WipSubRuleWfDO.class, "ruleId", ruleId));
        return WipSubRuleWfDO.batchBuildEntity(doList);
    }

    @Override
    public void insert(WipSubRuleWfEntity entity) {
        WipSubRuleWfDO wfDO = WipSubRuleWfDO.buildDO(entity);
        wipSubRuleWfMapper.insertSelective(wfDO);
    }

    @Override
    public void deleteByIdAndNode(String ruleId, String node) {
        Example example = new Example(WipSubRuleWfDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("ruleId", ruleId)
                .andEqualTo("node", AuditorNodeEnum.CURRENT_AUDITOR.getCode());
        wipSubRuleWfMapper.deleteByExample(example);
    }

    @Override
    public void batchInsert(List<WipSubRuleWfEntity> entityList) {
        List<WipSubRuleWfDO> wfDOList = WipSubRuleWfDO.batchBuildDO(entityList);
        batchMapper.insert(wfDOList);
    }

    @Override
    public void deleteByIds(String[] ids) {
        wipSubRuleWfMapper.deleteByIds(ids);
    }
}
