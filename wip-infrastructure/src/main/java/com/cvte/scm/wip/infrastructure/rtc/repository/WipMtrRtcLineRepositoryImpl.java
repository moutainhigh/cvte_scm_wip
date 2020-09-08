package com.cvte.scm.wip.infrastructure.rtc.repository;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcLineEntity;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject.WipMtrRtcLDO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcLineRepository;
import org.springframework.stereotype.Service;
import com.cvte.scm.wip.infrastructure.rtc.mapper.WipMtrRtcLMapper;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现类
 *
 * @author author
 * @since 2020-09-08
 */
@Service
public class WipMtrRtcLineRepositoryImpl
        extends WipBaseRepositoryImpl<WipMtrRtcLMapper, WipMtrRtcLDO, WipMtrRtcLineEntity>
        implements WipMtrRtcLineRepository {

}
