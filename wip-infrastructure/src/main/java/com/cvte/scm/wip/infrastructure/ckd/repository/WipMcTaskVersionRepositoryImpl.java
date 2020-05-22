package com.cvte.scm.wip.infrastructure.ckd.repository;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskVersionEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcTaskVersionRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.ckd.mapper.WipMcTaskVersionMapper;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcTaskVersionDO;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 *
 * @author zy
 * @since 2020-04-28
 */
@Repository
public class WipMcTaskVersionRepositoryImpl
        extends WipBaseRepositoryImpl<WipMcTaskVersionMapper, WipMcTaskVersionDO, WipMcTaskVersionEntity>
        implements WipMcTaskVersionRepository {

    @Autowired
    private WipMcTaskVersionMapper mapper;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public WipMcTaskVersionEntity getLastVersion(String taskId) {

        if (StringUtils.isBlank(taskId)) {
            throw new ParamsIncorrectException("必传参数不能为空");
        }

        Example example = new Example(WipMcTaskVersionDO.class);
        example.createCriteria().andEqualTo("mcTaskId", taskId);
        example.orderBy("versionNo").desc();
        List<WipMcTaskVersionDO> wipMcTaskVersions = mapper.selectByExample(example);

        if (CollectionUtils.isNotEmpty(wipMcTaskVersions)) {
            return modelMapper.map(wipMcTaskVersions.get(0), WipMcTaskVersionEntity.class);
        }

        return null;
    }

    public List<WipMcTaskVersionEntity> listWipMcTaskVersion(String taskId) {

        if (StringUtils.isBlank(taskId)) {
            throw new ParamsRequiredException("配料任务id不能为空");
        }

        Example example = new Example(WipMcTaskVersionEntity.class);
        example.createCriteria()
                .andEqualTo("mcTaskId", taskId);
        example.orderBy("crtTime").desc();
        return modelMapper.map(mapper.selectByExample(example), new TypeToken<List<WipMcTaskVersionEntity>>(){}.getType());
    }

    @Override
    protected List<WipMcTaskVersionDO> batchBuildDO(List<WipMcTaskVersionEntity> entityList) {
        return null;
    }

    @Override
    protected WipMcTaskVersionDO buildDO(WipMcTaskVersionEntity entity) {
        return null;
    }

    @Override
    protected WipMcTaskVersionEntity buildEntity(WipMcTaskVersionDO domain) {
        return null;
    }

    @Override
    protected List<WipMcTaskVersionEntity> batchBuildEntity(List<WipMcTaskVersionDO> entityList) {
        return null;
    }
}