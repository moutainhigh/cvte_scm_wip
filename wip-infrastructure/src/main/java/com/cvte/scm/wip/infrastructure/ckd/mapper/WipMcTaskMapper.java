package com.cvte.scm.wip.infrastructure.ckd.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcLineStatusQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskDeliveringStockView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.McTaskInfoView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipLineStatusView;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskView;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcTaskDO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-04-28
 */
public interface WipMcTaskMapper extends CommonMapper<WipMcTaskDO> {

    McTaskInfoView getMcTaskInfoView(@Param("mcTaskId") String mcTaskId);

    List<McTaskDeliveringStockView> listMcTaskDeliveringView(@Param("type") String type);

    List<WipLineStatusView> listWipLineStatusView(WipMcLineStatusQuery query);

    List<WipMcTaskView> listWipMcTask(WipMcTaskQuery query);

    List<String> listValidTaskIds(@Param("mcTaskIds") List<String> mcTaskIds);

    Boolean isSpecClient(@Param("mcTaskId") String mcTaskId);

    List<String> listAllTaskIdsOfOrder(@Param("sourceLineIds") List<String> sourceLineIds);

}