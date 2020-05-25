package com.cvte.scm.wip.infrastructure.ckd.repository;

import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineVersionQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineVersionView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskLineVersionEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcTaskLineVersionRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.ckd.mapper.WipMcTaskLineVersionMapper;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcTaskLineVersionDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author zy
 * @since 2020-04-28
 */
@Repository
public class WipMcTaskLineVersionRepositoryImpl
        extends WipBaseRepositoryImpl<WipMcTaskLineVersionMapper, WipMcTaskLineVersionDO,WipMcTaskLineVersionEntity>
        implements WipMcTaskLineVersionRepository {

    @Autowired
    private WipMcTaskLineVersionMapper wipMcTaskLineVersionMapper;

    @Override
    public List<WipMcTaskLineVersionView> listWipMcTaskLineVersionView(WipMcTaskLineVersionQuery query) {
        return wipMcTaskLineVersionMapper.listWipMcTaskLineVersionView(query);
    }

    @Override
    protected Class<WipMcTaskLineVersionEntity> getEntityClass() {
        return WipMcTaskLineVersionEntity.class;
    }

    @Override
    protected Class<WipMcTaskLineVersionDO> getDomainClass() {
        return WipMcTaskLineVersionDO.class;
    }
}