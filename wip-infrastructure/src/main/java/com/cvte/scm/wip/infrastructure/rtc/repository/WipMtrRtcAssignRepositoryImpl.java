package com.cvte.scm.wip.infrastructure.rtc.repository;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcAssignEntity;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject.WipMtrRtcAssignDO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcAssignRepository;
import org.springframework.stereotype.Service;
import com.cvte.scm.wip.infrastructure.rtc.mapper.WipMtrRtcAssignMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现类
 *
 * @author author
 * @since 2020-09-08
 */
@Service
public class WipMtrRtcAssignRepositoryImpl
        extends WipBaseRepositoryImpl<WipMtrRtcAssignMapper, WipMtrRtcAssignDO, WipMtrRtcAssignEntity>
        implements WipMtrRtcAssignRepository {

}
