package com.cvte.scm.wip.infrastructure.rtc.repository;

import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import com.cvte.scm.wip.infrastructure.rtc.mapper.dataobject.WipMtrRtcHDO;
import com.cvte.scm.wip.domain.core.rtc.repository.WipMtrRtcHeaderRepository;
import org.springframework.stereotype.Service;
import com.cvte.scm.wip.infrastructure.rtc.mapper.WipMtrRtcHMapper;

/**
 * 服务实现类
 *
 * @author author
 * @since 2020-09-08
 */
@Service
public class WipMtrRtcHeaderRepositoryImpl
        extends WipBaseRepositoryImpl<WipMtrRtcHMapper, WipMtrRtcHDO, WipMtrRtcHeaderEntity>
        implements WipMtrRtcHeaderRepository {

}
