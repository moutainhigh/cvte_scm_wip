package com.cvte.scm.wip.infrastructure.changebill.mapper;

import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillDO;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-05-22
 */
public interface WipCnBillMapper extends CommonMapper<WipCnBillDO> {

    WipCnBillDO selectByReqInsHeaderId(String reqInsHeaderId);

}