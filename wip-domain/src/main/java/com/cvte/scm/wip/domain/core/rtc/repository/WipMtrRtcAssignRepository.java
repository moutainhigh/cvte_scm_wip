package com.cvte.scm.wip.domain.core.rtc.repository;

import com.cvte.scm.wip.domain.common.repository.WipBaseRepository;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcAssignEntity;

import java.util.Collection;
import java.util.List;

/**
 * 服务类
 *
 * @author author
 * @since 2020-09-08
 */
public interface WipMtrRtcAssignRepository extends WipBaseRepository<WipMtrRtcAssignEntity> {

    List<WipMtrRtcAssignEntity> selectByLineIds(Collection<String> lineIds);

}
