package com.cvte.scm.wip.infrastructure.rtc.mapper;

import com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject.WipMtrRtcHDO;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-09-08
 */
public interface WipMtrRtcHMapper extends CommonMapper<WipMtrRtcHDO> {

    int selectCanceledOrderCount(@Param("moId") String moId);

}