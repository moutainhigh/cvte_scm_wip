package com.cvte.scm.wip.infrastructure.rtc.repository;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcPostLimitEntity;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject.WipMtrRtcPostLimitDO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcPostLimitRepository;
import org.springframework.stereotype.Service;
import com.cvte.scm.wip.infrastructure.rtc.mapper.WipMtrRtcPostLimitMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现类
 *
 * @author author
 * @since 2020-10-23
 */
@Service
@Transactional
public class WipMtrRtcPostLimitRepositoryImpl
        extends WipBaseRepositoryImpl<WipMtrRtcPostLimitMapper, WipMtrRtcPostLimitDO, WipMtrRtcPostLimitEntity>
        implements WipMtrRtcPostLimitRepository {

}
