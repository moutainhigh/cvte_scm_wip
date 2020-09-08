package com.cvte.scm.wip.domain.core.rtc.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.RtcLineQueryVO;

import java.math.BigDecimal;

/**
 * 服务类
 *
 * @author author
 * @since 2020-09-08
 */
public interface WipMtrRtcLineRepository extends WipBaseRepository<WipMtrRtcLineEntity> {

    BigDecimal sumNotPostQtyExceptCurrent(RtcLineQueryVO rtcLineQueryVO);

}
