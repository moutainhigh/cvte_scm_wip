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
    public List<WipMcReqToTaskEntity> selectList(WipMcReqToTaskEntity wipMcReqToTaskEntity) {
        List<WipMcReqToTaskDO> wipMcReqToTaskDOS =
                wipMcReqToTaskMapper.select(modelMapper.map(wipMcReqToTaskEntity, WipMcReqToTaskDO.class));
        return modelMapper.map(wipMcReqToTaskDOS, new TypeToken<List<WipMcReqToTaskEntity>>(){}.getType());
    }


    @Override
    protected List<WipMcReqToTaskDO> batchBuildDO(List<WipMcReqToTaskEntity> entityList) {
        return modelMapper.map(entityList, new TypeToken<List<WipMcReqToTaskDO>>(){}.getType());
    }

    @Override
    protected WipMcReqToTaskDO buildDO(WipMcReqToTaskEntity entity) {
        return null;
    }

    @Override
    protected WipMcReqToTaskEntity buildEntity(WipMcReqToTaskDO domain) {
        return null;
    }

    @Override
    protected List<WipMcReqToTaskEntity> batchBuildEntity(List<WipMcReqToTaskDO> entityList) {
        return null;
    }
}