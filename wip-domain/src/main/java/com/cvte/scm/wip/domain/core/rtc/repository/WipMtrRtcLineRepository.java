package com.cvte.scm.wip.domain.core.rtc.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineQueryVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 服务类
 *
 * @author author
 * @since 2020-09-08
 */
public interface WipMtrRtcLineRepository extends WipBaseRepository<WipMtrRtcLineEntity> {

    List<WipMtrRtcLineEntity> selectByHeaderId(String headerId);

    BigDecimal sumUnPostQtyExceptCurrent(WipMtrRtcLineQueryVO wipMtrRtcLineQueryVO);

}
