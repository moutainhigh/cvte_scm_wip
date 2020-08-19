package com.cvte.scm.wip.infrastructure.ckd.repository;

import com.cvte.csb.core.exception.client.params.SourceNotFoundException;
import com.cvte.csb.toolkit.CollectionUtils;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcLineStatusQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskDeliveringStockView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipLineStatusView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskEntity;
import com.cvte.scm.wip.domain.core.ckd.repository.WipMcTaskRepository;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.ckd.mapper.WipMcTaskMapper;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcTaskDO;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@Repository
public class WipMcTaskRepositoryImpl
        extends WipBaseRepositoryImpl<WipMcTaskMapper, WipMcTaskDO, WipMcTaskEntity>
        implements WipMcTaskRepository{

    @Autowired
    private WipMcTaskMapper wipMcTaskMapper;

    @Override
    protected Class<WipMcTaskEntity> getEntityClass() {
        return WipMcTaskEntity.class;
    }

    @Override
    protected Class<WipMcTaskDO> getDomainClass() {
        return WipMcTaskDO.class;
    }

    public WipMcTaskEntity getById(String mcTaskId) {
        WipMcTaskDO wipMcTaskDO = wipMcTaskMapper.selectByPrimaryKey(mcTaskId);
        if (ObjectUtils.isNull(wipMcTaskDO)) {
            log.error("[WipMcTaskRepositoryImpl][getById] id为{}的配料任务不存在", mcTaskId);
            throw new SourceNotFoundException("获取配料任务失败");
        }
        return modelMapper.map(wipMcTaskDO, WipMcTaskEntity.class);
    }

    public McTaskInfoView getMcTaskInfoView(String mcTaskId) {
       return wipMcTaskMapper.getMcTaskInfoView(mcTaskId);
    }

    public List<McTaskDeliveringStockView> listMcTaskDeliveringView(String type) {
        return wipMcTaskMapper.listMcTaskDeliveringView(type);
    }

    public List<WipLineStatusView> listWipLineStatusView(WipMcLineStatusQuery query) {
        return wipMcTaskMapper.listWipLineStatusView(query);
    }

    public List<WipMcTaskView> listWipMcTaskView(WipMcTaskQuery query) {
        return wipMcTaskMapper.listWipMcTask(query);
    }

    @Override
    public List<String> listValidTaskIds(List mcTaskIds) {
        return wipMcTaskMapper.listValidTaskIds(mcTaskIds);
    }



    @Override
    public Integer getCurrentSerialNo(String serialNoStartWith) {
        Example example = new Example(WipMcTaskDO.class);
        example.createCriteria()
                .andLike("mcTaskNo", serialNoStartWith + "%");
        example.orderBy("mcTaskNo").desc();
        example.selectProperties("mcTaskNo");
        List<WipMcTaskDO> wipMcTasks = mapper.selectByExample(example);
        if (CollectionUtils.isEmpty(wipMcTasks)) {
            return 0;
        }

        String mcTaskNo = wipMcTasks.get(0).getMcTaskNo();

        return Integer.parseInt(mcTaskNo.substring(mcTaskNo.length() - 3));
    }

    public List<WipMcTaskEntity> listWipMcTask(WipMcTaskQuery query) {

        Example example = new Example(WipMcTaskDO.class);
        Example.Criteria criteria = example.createCriteria();

        if (CollectionUtils.isNotEmpty(query.getTaskIds())) {
            criteria.andIn("mcTaskId", query.getTaskIds());
        }

        if (CollectionUtils.isNotEmpty(query.getMcTaskNos())) {
            criteria.andIn("mcTaskNo", query.getMcTaskNos());
        }

        return modelMapper.map(mapper.selectByExample(example), new TypeToken<List<WipMcTaskEntity>>(){}.getType());
    }

    public Boolean isSpecClient(String mcTaskId) {
        return mapper.isSpecClient(mcTaskId);
    }


}