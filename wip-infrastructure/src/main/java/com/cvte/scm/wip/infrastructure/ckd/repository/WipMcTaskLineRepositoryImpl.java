package com.cvte.scm.wip.infrastructure.ckd.repository;

import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskLineEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcTaskLineRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.ckd.mapper.WipMcTaskLineMapper;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcTaskLineDO;
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
public class WipMcTaskLineRepositoryImpl
        extends WipBaseRepositoryImpl<WipMcTaskLineMapper, WipMcTaskLineDO, WipMcTaskLineEntity>
        implements WipMcTaskLineRepository {

    @Autowired
    private WipMcTaskLineMapper wipMcTaskLineMapper;

    @Override
    public List<WipMcTaskLineView> listWipMcTaskLineView(WipMcTaskLineQuery query) {
        return wipMcTaskLineMapper.listWipMcTaskLineView(query);
    }



    @Override
    public List<WipMcTaskLineEntity> listWipMcTaskLine(WipMcTaskLineQuery query) {

        Example example = new Example(WipMcTaskLineDO.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("lineStatus", query.getLineStatus());

        if (CollectionUtils.isNotEmpty(query.getTaskIds())) {

            criteria.andIn("mcTaskId", query.getTaskIds());
        }

        if (CollectionUtils.isNotEmpty(query.getSourceLineIds())) {
            criteria.andIn("sourceLineId", query.getSourceLineIds());
        }


        return modelMapper.map(wipMcTaskLineMapper.selectByExample(example),
                new TypeToken<List<WipMcTaskLineEntity>>(){}.getType());
    }

    @Override
    protected Class<WipMcTaskLineEntity> getEntityClass() {
        return WipMcTaskLineEntity.class;
    }

    @Override
    protected Class<WipMcTaskLineDO> getDomainClass() {
        return WipMcTaskLineDO.class;
    }
}