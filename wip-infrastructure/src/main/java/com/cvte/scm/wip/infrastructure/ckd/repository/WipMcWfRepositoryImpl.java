package com.cvte.scm.wip.infrastructure.ckd.repository;

import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcWfQuery;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcWfEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcWfRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.ckd.mapper.WipMcWfMapper;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcWfDO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 *
 * @author zy
 * @since 2020-04-29
 */
@Repository
public class WipMcWfRepositoryImpl
        extends WipBaseRepositoryImpl<WipMcWfMapper, WipMcWfDO, WipMcWfEntity>
        implements WipMcWfRepository {

    @Autowired
    private WipMcWfMapper mapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<WipMcWfEntity> listWipMcWf(WipMcWfQuery query) {

        Example example = new Example(WipMcWfEntity.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("mcTaskId", query.getMcTaskId());

        if (CollectionUtils.isNotEmpty(query.getWfIds())) {
            criteria.andIn("wfId", query.getWfIds());
        }

        example.orderBy("crtTime").desc();
        return modelMapper.map(mapper.selectByExample(example), new TypeToken<List<WipMcWfEntity>>(){}.getType());
    }

    @Override
    protected List<WipMcWfDO> batchBuildDO(List<WipMcWfEntity> entityList) {
        return null;
    }

    @Override
    protected WipMcWfDO buildDO(WipMcWfEntity entity) {
        return null;
    }

    @Override
    protected WipMcWfEntity buildEntity(WipMcWfDO domain) {
        return null;
    }

    @Override
    protected List<WipMcWfEntity> batchBuildEntity(List<WipMcWfDO> entityList) {
        return null;
    }
}