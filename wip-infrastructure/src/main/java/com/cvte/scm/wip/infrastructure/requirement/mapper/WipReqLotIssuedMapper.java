package com.cvte.scm.wip.infrastructure.requirement.mapper;

import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject.WipReqLotIssuedDO;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-01-17
 */
public interface WipReqLotIssuedMapper extends CommonMapper<WipReqLotIssuedDO> {

    int selectCnBillTypeLot(@Param("organizationId") String organizationId,
                            @Param("headerId") String headerId,
                            @Param("itemKey") String itemKey);

}