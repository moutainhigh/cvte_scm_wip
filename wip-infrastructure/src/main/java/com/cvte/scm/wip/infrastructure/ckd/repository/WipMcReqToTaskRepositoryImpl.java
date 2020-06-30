package com.cvte.scm.wip.infrastructure.ckd.repository;

import com.cvte.scm.wip.domain.core.ckd.entity.WipMcReqToTaskEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcReqToTaskRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.ckd.mapper.WipMcReqToTaskMapper;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcReqToTaskDO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *
 * @author zy
 * @since 2020-04-28
 */
@Repository
public class WipMcReqToTaskRepositoryImpl
        extends WipBaseRepositoryImpl<WipMcReqToTaskMapper, WipMcReqToTaskDO, WipMcReqToTaskEntity>
        implements WipMcReqToTaskRepository {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WipMcReqToTaskMapper wipMcReqToTaskMapper;

    @Override
    protected Class<WipMcReqToTaskEntity> getEntityClass() {
        return WipMcReqToTaskEntity.class;
    }

    @Override
    protected Class<WipMcReqToTaskDO> getDomainClass() {
        return WipMcReqToTaskDO.class;
    }

    @Override
    public List<WipMcReqToTaskEntity> selectList(WipMcReqToTaskEntity wipMcReqToTaskEntity) {
        List<WipMcReqToTaskDO> wipMcReqToTaskDOS =
                wipMcReqToTaskMapper.select(modelMapper.map(wipMcReqToTaskEntity, WipMcReqToTaskDO.class));
        return modelMapper.map(wipMcReqToTaskDOS, new TypeToken<List<WipMcReqToTaskEntity>>(){}.getType());
    }



}