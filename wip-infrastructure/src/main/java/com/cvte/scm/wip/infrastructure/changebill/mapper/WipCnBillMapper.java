package com.cvte.scm.wip.infrastructure.changebill.mapper;

import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillDO;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-05-22
 */
public interface WipCnBillMapper extends CommonMapper<WipCnBillDO> {

    WipCnBillDO selectByReqInsHeaderId(String reqInsHeaderId);

    List<WipCnBillDO> selectSyncFailedBills(@Param("errMsgList") List<String> errMsgList, @Param("factoryId") String factoryId);

}