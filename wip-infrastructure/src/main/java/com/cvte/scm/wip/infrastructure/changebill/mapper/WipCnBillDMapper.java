package com.cvte.scm.wip.infrastructure.changebill.mapper;

import com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject.WipCnBillDetailDO;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-05-22
 */
public interface WipCnBillDMapper extends CommonMapper<WipCnBillDetailDO> {

    List<WipCnBillDetailDO> selectSyncFailedDetails(@Param("errMsgList") List<String> errMsgList);

}