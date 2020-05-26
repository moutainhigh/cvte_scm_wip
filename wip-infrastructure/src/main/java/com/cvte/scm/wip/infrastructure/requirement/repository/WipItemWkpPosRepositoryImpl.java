package com.cvte.scm.wip.infrastructure.requirement.repository;

import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.scm.wip.domain.core.requirement.dto.query.QueryWipItemWkpPosVO;
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

        if (CollectionUtils.isNotEmpty(query.getItemCodes())) {
            criteria.andIn("itemCode",  query.getItemCodes());
        }

        return modelMapper.map(mapper.selectByExample(example), new TypeToken<List<WipItemWkpPosEntity>>(){}.getType());
    }

}
