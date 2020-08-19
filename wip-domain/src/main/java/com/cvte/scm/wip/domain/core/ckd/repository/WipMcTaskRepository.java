package com.cvte.scm.wip.domain.core.ckd.repository;


import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcLineStatusQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskDeliveringStockView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipLineStatusView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskView;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskEntity;

import java.util.List;

/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-04-28
 */
public interface WipMcTaskRepository extends WipBaseRepository<WipMcTaskEntity> {

    WipMcTaskEntity getById(String mcTaskId);

    McTaskInfoView getMcTaskInfoView(String mcTaskId);

    List<McTaskDeliveringStockView> listMcTaskDeliveringView(String type);

    List<WipLineStatusView> listWipLineStatusView(WipMcLineStatusQuery query);

    List<WipMcTaskView> listWipMcTaskView(WipMcTaskQuery query);

    List<String> listValidTaskIds(List<String> mcTaskIds);

    Integer getCurrentSerialNo(String serialNoStartWith);

    List<WipMcTaskEntity> listWipMcTask(WipMcTaskQuery query);


    /**
     * 是否指定的特殊客户
     *
     * @param mcTaskId
     * @return java.lang.Boolean
     **/
    Boolean isSpecClient(String mcTaskId);


}