package com.cvte.scm.wip.domain.common.log.service;

import com.cvte.csb.base.commons.OperatingUser;
import com.cvte.csb.base.context.CurrentContext;
import com.cvte.csb.core.exception.client.params.ParamsRequiredException;
import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.domain.common.log.dto.WipLogDTO;
import com.cvte.scm.wip.domain.common.log.entity.WipOperationLogEntity;
import com.cvte.scm.wip.domain.common.log.repository.WipOperationLogRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 服务实现类
 *
 * @author zy
 * @since 2020-05-22
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipOperationLogService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WipOperationLogRepository repository;

    public void addLog(WipLogDTO wipLogDTO, OperatingUser operatingUser) {

        if (ObjectUtils.isNull(operatingUser)) {
            throw new ParamsRequiredException("操作人不可为空");
        }

        WipOperationLogEntity wipOperationLog = modelMapper.map(wipLogDTO, WipOperationLogEntity.class);
        wipOperationLog.setId(UUIDUtils.getUUID())
                .setCrtTime(new Date())
                .setCrtName(operatingUser.getName())
                .setCrtUser(operatingUser.getAccount());
        repository.insertSelective(wipOperationLog);
    }

    public void addLog(WipLogDTO wipLogDTO) {
        addLog(wipLogDTO, CurrentContext.getCurrentOperatingUser());
    }
}
