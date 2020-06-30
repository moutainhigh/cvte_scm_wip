package com.cvte.scm.wip.infrastructure.subrule.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.core.subrule.valueobject.EntryVO;
import com.cvte.scm.wip.domain.core.subrule.valueobject.WipSubRuleLotDetailVO;
import com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject.WipSubRuleAdaptDO;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author jf
 * @since 2020-02-17
 */
public interface WipSubRuleAdaptMapper extends CommonMapper<WipSubRuleAdaptDO> {

    /* 获取指定组织ID的生产批次明细 */
    List<WipSubRuleLotDetailVO> getSubRuleLotDetailData(@Param("condition") String condition);

    /* 根据已有的生产批次号列表，获取其中已备料的生产批次号 */
    List<String> getPreparedLotNos(@Param("organizationId") String organizationId, @Param("lotNoList") Collection<String> lotNoList);

    /* 根据工厂编号获取相应的工厂名称 */
    Set<EntryVO> getFactoryNameData(@Param("factoryIds") Collection<String> factoryIds);
}