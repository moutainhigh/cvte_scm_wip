package com.cvte.scm.wip.domain.core.rtc.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;

import java.util.List;

/**
 * 服务类
 *
 * @author author
 * @since 2020-09-08
 */
public interface WipMtrRtcHeaderRepository extends WipBaseRepository<WipMtrRtcHeaderEntity> {

    List<WipMtrRtcHeaderEntity> selectUnPost(WipMtrRtcHeaderEntity rtcHeader);

    int selectCanceledOrderCount(String moId);

}
