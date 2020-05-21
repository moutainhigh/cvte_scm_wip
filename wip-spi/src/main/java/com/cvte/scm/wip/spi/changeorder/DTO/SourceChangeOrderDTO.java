package com.cvte.scm.wip.spi.changeorder.DTO;

import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderBuildVO;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderDetailBuildVO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
  * 更改单的平铺数据结构
  * @author  : xueyuting
  * @since    : 2020/5/21 11:21
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class SourceChangeOrderDTO {

    private String billId;

    private String billNo;

    private String organizationId;

    private String billType;

    private String moId;

    private String billStatus;

    private Date enableDate;

    private Date disableDate;

    private String detailId;

    private String moLotNo;

    private String detailStatus;

    private String wkpNo;

    private String itemIdOld;

    private String itemIdNew;

    private String operationType;

    private String posNo;

    private Date detailEnableDate;

    private Date detailDisableDate;

    public static ChangeOrderBuildVO buildHeaderVO(SourceChangeOrderDTO changeOrderDTO) {
        ChangeOrderBuildVO orderBuildVO = new ChangeOrderBuildVO();
        orderBuildVO.setBillId(changeOrderDTO.getBillId())
                .setBillNo(changeOrderDTO.getBillNo())
                .setOrganizationId(changeOrderDTO.getOrganizationId())
                .setBillType(changeOrderDTO.getBillType())
                .setMoId(changeOrderDTO.getMoId())
                .setBillStatus(changeOrderDTO.getBillStatus())
                .setEnableDate(changeOrderDTO.getEnableDate())
                .setDisableDate(changeOrderDTO.getDisableDate());
        return orderBuildVO;
    }

    public static ChangeOrderDetailBuildVO buildDetailVO(SourceChangeOrderDTO changeOrderDTO) {
        ChangeOrderDetailBuildVO detailBuildVO = new ChangeOrderDetailBuildVO();
        detailBuildVO.setBillId(changeOrderDTO.getBillId())
                .setOrganizationId(changeOrderDTO.getOrganizationId())
                .setDetailId(changeOrderDTO.getDetailId())
                .setMoLotNo(changeOrderDTO.getMoLotNo())
                .setDetailStatus(changeOrderDTO.getDetailStatus())
                .setWkpNo(changeOrderDTO.getWkpNo())
                .setItemIdOld(changeOrderDTO.getItemIdOld())
                .setItemIdNew(changeOrderDTO.getItemIdNew())
                .setOperationType(changeOrderDTO.getOperationType())
                .setPosNo(changeOrderDTO.getPosNo())
                .setEnableDate(changeOrderDTO.getDetailEnableDate())
                .setDisableDate(changeOrderDTO.getDetailDisableDate());
        return detailBuildVO;
    }

}
