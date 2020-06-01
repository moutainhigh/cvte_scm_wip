package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.core.requirement.valueobject.QueryWipItemWkpPosVO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipItemWkpPosEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipItemWkpPosRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.requirement.mapper.WipItemWkpPosMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipItemWkpPosDO;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-22 14:32
 **/
@Repository
public class WipItemWkpPosRepositoryImpl
        extends WipBaseRepositoryImpl<WipItemWkpPosMapper, WipItemWkpPosDO, WipItemWkpPosEntity>
        implements WipItemWkpPosRepository {

    @Override
    protected Class<WipItemWkpPosEntity> getEntityClass() {
        return WipItemWkpPosEntity.class;
    }

    @Override
    protected Class<WipItemWkpPosDO> getDomainClass() {
        return WipItemWkpPosDO.class;
    }

    @Override
    public List<WipItemWkpPosEntity> listWipItemWkpPosEntity(QueryWipItemWkpPosVO query) {
        Example example = new Example(WipItemWkpPosDO.class);
        Example.Criteria criteria = example.createCriteria();

        if (ObjectUtils.isNotNull(query.getQueryDate())) {
            criteria.andLessThanOrEqualTo("beginDate", query.getQueryDate());
            criteria.andGreaterThan("endDate", query.getQueryDate());
        }
        if (CollectionUtils.isNotEmpty(query.getItemCodes())) {
            criteria.andIn("itemCode",  query.getItemCodes());
        }

        if (CollectionUtils.isNotEmpty(query.getOrganizationIds())) {
            criteria.andIn("organizationId", query.getOrganizationIds());
        }

        if (CollectionUtils.isNotEmpty(query.getProductModels())) {
            criteria.andIn("productModel", query.getProductModels());
        }
        return modelMapper.map(mapper.selectByExample(example), new TypeToken<List<WipItemWkpPosEntity>>(){}.getType());
    }

}
