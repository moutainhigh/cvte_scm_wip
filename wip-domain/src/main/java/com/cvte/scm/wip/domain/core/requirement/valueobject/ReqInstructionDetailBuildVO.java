package com.cvte.scm.wip.domain.core.requirement.valueobject;

import com.cvte.csb.toolkit.UUIDUtils;
import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.changeorder.entity.ChangeOrderDetailEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 16:26
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ReqInstructionDetailBuildVO implements VO {

    private String instructionDetailId;

    private String organizationId;

    private String sourceChangeDetailId;

    private String moLotNo;

    private String itemIdOld;

    private String itemIdNew;

    private String wkpNo;

    private String posNo;

    private BigDecimal itemQty;

    private String operationType;

    private String insStatus;

    private Date enableDate;

    private Date disableDate;

    public static ReqInstructionDetailBuildVO buildVO(ChangeOrderDetailEntity orderDetailEntity) {
        ReqInstructionDetailBuildVO detailBuildVO = new ReqInstructionDetailBuildVO();
        detailBuildVO.setOrganizationId(orderDetailEntity.getOrganizationId())
                .setSourceChangeDetailId(orderDetailEntity.getDetailId())
                .setMoLotNo(orderDetailEntity.getMoLotNo())
                .setPosNo(orderDetailEntity.getPosNo())
                .setItemIdOld(orderDetailEntity.getItemIdOld())
                .setItemIdNew(orderDetailEntity.getItemIdNew())
                .setWkpNo(orderDetailEntity.getWkpNo())
                .setItemQty(orderDetailEntity.getItemQty())
                .setEnableDate(orderDetailEntity.getEnableDate())
                .setDisableDate(orderDetailEntity.getDisableDate());
        return detailBuildVO;
    }

}
