package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLotProcessVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/4 18:10
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class WipReqLotProcessDomainService implements DomainService {

    private WipReqHeaderService wipReqHeaderService;
    private WipReqLotIssuedService wipReqLotIssuedService;


    public WipReqLotProcessDomainService(WipReqHeaderService wipReqHeaderService, WipReqLotIssuedService wipReqLotIssuedService) {
        this.wipReqHeaderService = wipReqHeaderService;
        this.wipReqLotIssuedService = wipReqLotIssuedService;
    }

    public void process(WipReqLotProcessEntity lotProcessEntity) {
        WipReqHeaderEntity headerEntity = wipReqHeaderService.getByHeaderId(lotProcessEntity.getWipEntityId());
        List<WipReqLotIssuedEntity> lotIssuedEntityList = wipReqLotIssuedService.selectLockedByKey(WipReqLotProcessVO.build(lotProcessEntity));

        lotProcessEntity.process(Objects.nonNull(headerEntity), ListUtil.notEmpty(lotIssuedEntityList));
    }

}
