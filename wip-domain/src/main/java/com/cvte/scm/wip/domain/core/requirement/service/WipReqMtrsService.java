package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqMtrsEntity;
import com.cvte.scm.wip.domain.core.requirement.repository.WipReqMtrsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.util.Objects.isNull;

/**
 * @author jf
 * @since 2020-03-04
 */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipReqMtrsService {

    private WipReqMtrsRepository wipReqMtrsRepository;

    public WipReqMtrsService(WipReqMtrsRepository wipReqMtrsRepository) {
        this.wipReqMtrsRepository = wipReqMtrsRepository;
    }

    public boolean canSubstitute(WipReqMtrsEntity wipReqMtrs) {
        if (isNull(wipReqMtrs) || isNull(wipReqMtrs.getHeaderId()) || isNull(wipReqMtrs.getWkpNo())
                || isNull(wipReqMtrs.getOrganizationId()) || isNull(wipReqMtrs.getItemNo())) {
            return false;
        }
        return wipReqMtrsRepository.selectCount(wipReqMtrs) > 0;
    }

    public List<WipReqMtrsEntity> getAllMtrs(String headerId, String itemId){
        List<WipReqMtrsEntity> allMtrs = wipReqMtrsRepository.getAllMtrs(headerId, itemId);
        return allMtrs;
    }
}