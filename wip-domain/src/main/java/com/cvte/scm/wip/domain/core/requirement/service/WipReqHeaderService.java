package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.utils.CurrentContextUtils;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqHeaderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 服务实现类
 *
 * @author author
 * @since 2019-12-30
 */
@Service
@Slf4j
@Transactional(transactionManager = "pgTransactionManager")
public class WipReqHeaderService {

    private WipReqHeaderRepository wipReqHeaderRepository;

    public WipReqHeaderService(WipReqHeaderRepository wipReqHeaderRepository) {
        this.wipReqHeaderRepository = wipReqHeaderRepository;
    }

    public WipReqHeaderEntity getByHeaderId(String headerId) {
        if (StringUtils.isBlank(headerId)) {
            throw new ParamsIncorrectException("工单ID不可为空");
        }
        return wipReqHeaderRepository.getByHeaderId(headerId);
    }

    public WipReqHeaderEntity getBySourceId(String sourceId) {
        return wipReqHeaderRepository.getBySourceId(sourceId);
    }

    public String updateWipReqHeaders(List<WipReqHeaderEntity> wipReqHeaderList, ExecutionModeEnum mode) {
        List<WipReqHeaderEntity> updateWipReqHeaders = new ArrayList<>();
        String errorMessage = EntityUtils.accumulate(wipReqHeaderList, header -> wipReqHeaderRepository.validateAndGetUpdateDataHelper(header, updateWipReqHeaders));
        EntityUtils.handleErrorMessage(errorMessage, mode);
        wipReqHeaderList.forEach(line -> EntityUtils.writeStdUpdInfoToEntity(line, CurrentContextUtils.getOrDefaultUserId("SCM-WIP")));
        wipReqHeaderRepository.batchUpdate(wipReqHeaderList);
        return errorMessage;
    }

}