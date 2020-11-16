package com.cvte.scm.wip.infrastructure.audit.repository;

import com.cvte.scm.wip.domain.common.audit.entity.WipAuditLogEntity;
import com.cvte.scm.wip.domain.common.audit.repository.WipAuditLogRepository;
import com.cvte.scm.wip.infrastructure.audit.mapper.WipAuditLogMapper;
import com.cvte.scm.wip.infrastructure.audit.mapper.dataobject.WipAuditLogDO;
import com.cvte.scm.wip.infrastructure.base.WipBaseRepositoryImpl;
import org.springframework.stereotype.Service;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/10/10 14:54
 */
@Service
public class WipAuditLogRepositoryImpl
        extends WipBaseRepositoryImpl<WipAuditLogMapper, WipAuditLogDO, WipAuditLogEntity>
        implements WipAuditLogRepository {
}
