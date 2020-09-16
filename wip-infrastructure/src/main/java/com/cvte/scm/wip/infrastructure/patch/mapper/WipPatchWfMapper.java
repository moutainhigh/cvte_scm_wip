package com.cvte.scm.wip.infrastructure.patch.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.patch.mapper.dataobject.WipPatchWfDO;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-07-25
 */
public interface WipPatchWfMapper extends CommonMapper<WipPatchWfDO> {

    //@SelectKey(statement = "SELECT currval('wip_patch_wf_id_seq')", keyProperty = "wipPatchWfDO.id", before = false, resultType = Integer.class)
    void insertBatchReturnId(WipPatchWfDO wipPatchWfDO);

}