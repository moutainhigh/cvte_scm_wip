package com.cvte.scm.wip.spi.changebill.DTO;

import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO;
import lombok.Data;

import java.util.Date;

/**
  * 更改单的平铺数据结构
  * @author  : xueyuting
  * @since    : 2020/5/21 11:21
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class SourceChangeBillDTO {

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

    public static ChangeBillBuildVO buildHeaderVO(SourceChangeBillDTO changeBillDTO) {
        ChangeBillBuildVO billBuildVO = new ChangeBillBuildVO();
        billBuildVO.setBillId(changeBillDTO.getBillId())
                .setBillNo(changeBillDTO.getBillNo())
                .setOrganizationId(changeBillDTO.getOrganizationId())
                .setBillType(changeBillDTO.getBillType())
                .setMoId(changeBillDTO.getMoId())
                .setBillStatus(changeBillDTO.getBillStatus())
                .setEnableDate(changeBillDTO.getEnableDate())
                .setDisableDate(changeBillDTO.getDisableDate());
        return billBuildVO;
    }

    public static ChangeBillDetailBuildVO buildDetailVO(SourceChangeBillDTO changeBillDTO) {
        ChangeBillDetailBuildVO detailBuildVO = new ChangeBillDetailBuildVO();
        detailBuildVO.setBillId(changeBillDTO.getBillId())
                .setOrganizationId(changeBillDTO.getOrganizationId())
                .setDetailId(changeBillDTO.getDetailId())
                .setMoLotNo(changeBillDTO.getMoLotNo())
                .setDetailStatus(changeBillDTO.getDetailStatus())
                .setWkpNo(changeBillDTO.getWkpNo())
                .setItemIdOld(changeBillDTO.getItemIdOld())
                .setItemIdNew(changeBillDTO.getItemIdNew())
                .setOperationType(changeBillDTO.getOperationType())
                .setPosNo(changeBillDTO.getPosNo())
                .setEnableDate(changeBillDTO.getDetailEnableDate())
                .setDisableDate(changeBillDTO.getDetailDisableDate());
        return detailBuildVO;
    }

}
