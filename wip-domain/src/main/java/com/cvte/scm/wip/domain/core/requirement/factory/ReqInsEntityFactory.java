package com.cvte.scm.wip.domain.core.requirement.factory;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:27
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class ReqInsEntityFactory implements DomainFactory<ReqInsBuildVO, ReqInsEntity> {

    @Override
    public ReqInsEntity perfect(ReqInsBuildVO vo) {
        ReqInsEntity headerEntity = ReqInsEntity.get();
        headerEntity.setInsHeaderId(vo.getInsHeaderId())
                .setSourceChangeBillId(vo.getSourceChangeBillId())
                .setStatus(vo.getInsHeaderStatus())
                .setAimHeaderId(vo.getAimHeaderId())
                .setAimReqLotNo(vo.getAimReqLotNo())
                .setEnableDate(vo.getEnableDate())
                .setDisableDate(vo.getDisableDate());
        return headerEntity;
    }

}
