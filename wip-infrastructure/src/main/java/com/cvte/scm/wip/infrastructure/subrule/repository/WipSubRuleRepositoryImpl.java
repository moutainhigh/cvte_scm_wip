package com.cvte.scm.wip.infrastructure.subrule.repository;

import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleEntity;
import com.cvte.scm.wip.domain.core.subrule.repository.WipSubRuleRepository;
import com.cvte.scm.wip.domain.core.subrule.valueobject.enums.SubRuleStatusEnum;
import com.cvte.scm.wip.infrastructure.subrule.mapper.WipSubRuleMapper;
import com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject.WipSubRuleDO;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 21:12
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Repository
public class WipSubRuleRepositoryImpl implements WipSubRuleRepository {

    /**
     * 一个位标识，用于获取枚举 {@link SubRuleStatusEnum} 中 "已作废和已失效" 的 code 值列表。
     */
    private static final int INVALID_EXPIRED = 24;

    private WipSubRuleMapper wipSubRuleMapper;

    public WipSubRuleRepositoryImpl(WipSubRuleMapper wipSubRuleMapper) {
        this.wipSubRuleMapper = wipSubRuleMapper;
    }

    @Override
    public Integer update(WipSubRuleEntity ruleEntity) {
        WipSubRuleDO ruleDO = WipSubRuleDO.buildDO(ruleEntity);
        return wipSubRuleMapper.updateByPrimaryKeySelective(ruleDO);
    }

    @Override
    public void updateByRuleId(WipSubRuleEntity entity, List<String> ruleIdList) {
        Example example = new Example(WipSubRuleDO.class);
        example.createCriteria().andIn("ruleId", ruleIdList);
        WipSubRuleDO ruleDO = WipSubRuleDO.buildDO(entity);
        wipSubRuleMapper.updateByExampleSelective(ruleDO, example);
    }

    @Override
    public Integer expire() {
        /* 过了失效时间的临时代用单规则，状态自动变成"已失效" */
        Example example = new Example(WipSubRuleDO.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotIn("ruleStatus", CodeableEnumUtils.getCodesByOrdinalFlag(INVALID_EXPIRED, SubRuleStatusEnum.class));
        criteria.andCondition("now() > coalesce(disable_time, now() + '1ms')");
        WipSubRuleDO wipSubRule = new WipSubRuleDO().setRuleStatus(SubRuleStatusEnum.EXPIRED.getCode());
        EntityUtils.writeStdUpdInfoToEntity(wipSubRule, EntityUtils.getWipUserId());
        return wipSubRuleMapper.updateByExampleSelective(wipSubRule, example);
    }

    @Override
    public List<WipSubRuleEntity> selectList(WipSubRuleEntity ruleEntity) {
        WipSubRuleDO ruleDO = WipSubRuleDO.buildDO(ruleEntity);
        List<WipSubRuleDO> ruleDOList = wipSubRuleMapper.select(ruleDO);
        return WipSubRuleDO.batchBuildEntity(ruleDOList);
    }

    @Override
    public WipSubRuleEntity selectByRuleId(String ruleId) {
        WipSubRuleDO querySubRule = new WipSubRuleDO().setRuleId(ruleId);
        WipSubRuleDO subRule = wipSubRuleMapper.selectByPrimaryKey(querySubRule);
        return WipSubRuleDO.buildEntity(subRule);
    }

    @Override
    public List<WipSubRuleEntity> selectByRuleNo(String ruleNo) {
        List<WipSubRuleDO> ruleDOList = wipSubRuleMapper.selectByExample(EntityUtils.getExample(WipSubRuleDO.class, "ruleNo", ruleNo));
        return WipSubRuleDO.batchBuildEntity(ruleDOList);
    }

    @Override
    public WipSubRuleEntity selectByRuleIdAndStatus(String ruleId, String status) {
        WipSubRuleDO querySubRule = new WipSubRuleDO().setRuleId(ruleId).setRuleStatus(status);
        WipSubRuleDO subRule = wipSubRuleMapper.selectByPrimaryKey(querySubRule);
        return WipSubRuleDO.buildEntity(subRule);
    }

    @Override
    public List<WipSubRuleEntity> selectByRuleIdAndStatus(List<String> ruleIdList, List<Object> statusList) {
        Example example = new Example(WipSubRuleDO.class);
        example.createCriteria().andIn("ruleId", ruleIdList)
                .andIn("ruleStatus", statusList);
        List<WipSubRuleDO> subRuleList = wipSubRuleMapper.selectByExample(example);
        return WipSubRuleDO.batchBuildEntity(subRuleList);
    }

    @Override
    public void insert(WipSubRuleEntity entity) {
        WipSubRuleDO ruleDO = WipSubRuleDO.buildDO(entity);
        wipSubRuleMapper.insertSelective(ruleDO);
    }
}
