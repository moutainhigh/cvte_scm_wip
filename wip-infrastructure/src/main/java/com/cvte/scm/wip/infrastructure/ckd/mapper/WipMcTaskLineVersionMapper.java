package com.cvte.scm.wip.infrastructure.ckd.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcTaskLineVersionQuery;
import com.cvte.scm.wip.domain.core.ckd.dto.view.WipMcTaskLineVersionView;
import com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject.WipMcTaskLineVersionDO;

import java.util.List;

/**
 * Mapper接口
 *
 * @author zy
 * @since 2020-04-28
 */
public interface WipMcTaskLineVersionMapper extends CommonMapper<WipMcTaskLineVersionDO> {


    List<WipMcTaskLineVersionView> listWipMcTaskLineVersionView(WipMcTaskLineVersionQuery query);
}