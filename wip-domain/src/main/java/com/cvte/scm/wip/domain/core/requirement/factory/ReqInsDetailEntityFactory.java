package com.cvte.scm.wip.domain.core.requirement.factory;

import com.cvte.scm.wip.common.base.domain.DomainFactory;
import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsDetailEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsDetailBuildVO;
import org.springframework.stereotype.Component;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 16:38
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Component
public class ReqInsDetailEntityFactory implements DomainFactory<ReqInsDetailBuildVO, ReqInsDetailEntity> {

    @Override
    public ReqInsDetailEntity perfect(ReqInsDetailBuildVO vo) {
        ReqInsDetailEntity detailEntity = ReqInsDetailEntity.get();
        detailEntity.setInsDetailId(vo.getInsDetailId())
                .setInsHeaderId(vo.getInsHeaderId())
                .setOrganizationId(vo.getOrganizationId())
                .setSourceDetailId(vo.getSourceChangeDetailId())
                .setMoLotNo(vo.getMoLotNo())
                .setItemIdOld(vo.getItemIdOld())
                .setItemIdNew(vo.getItemIdNew())
                .setWkpNo(vo.getWkpNo())
                .setPosNo(vo.getPosNo())
                .setItemQty(vo.getItemQty())
                .setItemUnitQty(vo.getItemUnitQty())
                .setOperationType(vo.getOperationType())
                .setInsStatus(vo.getInsStatus())
                .setEnableDate(vo.getEnableDate())
                .setDisableDate(vo.getDisableDate())
                .setIssueFlag(vo.getIssueFlag());
        return detailEntity;
    }

}
