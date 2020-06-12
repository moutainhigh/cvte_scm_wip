package com.cvte.scm.wip.infrastructure.ckd.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineView;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcTaskLineDO;

import java.util.List;


/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-04-28
 */
public interface WipMcTaskLineMapper extends CommonMapper<WipMcTaskLineDO> {


    List<WipMcTaskLineView> listWipMcTaskLineView(WipMcTaskLineQuery query);

}