package com.cvte.scm.wip.infrastructure.rtc.mapper;

import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineQueryVO;
import com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject.WipMtrRtcLDO;
import com.cvte.csb.jdbc.mybatis.mapper.CommonMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * Mapper接口
 *
 * @author author
 * @since 2020-09-08
 */
public interface WipMtrRtcLMapper extends CommonMapper<WipMtrRtcLDO> {

    BigDecimal sumQtyExceptCurrent(@Param("queryVO") WipMtrRtcLineQueryVO queryVO);

    List<WipReqItemVO> batchSumUnPostQtyExceptCurrent(@Param("queryVO") WipMtrRtcLineQueryVO queryVO);

}