package com.cvte.scm.wip.spi.changebill.DTO;

import com.alibaba.fastjson.annotation.JSONField;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.enums.ChangeBillStatusEnum;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

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

    @JSONField(name = "BILL_NO")
    private String billNo;

    @JSONField(name = "BILL_SOURCE_NO")
    private String billSourceNo;

    @JSONField(name = "BILL_ORGANIZATION_ID")
    private String organizationId;

    @JSONField(name = "BILL_TYPE")
    private String billType;

    @JSONField(name = "BILL_MO_ID")
    private String moId;

    @JSONField(name = "BILL_STATUS")
    private String billStatus;

    @JSONField(name = "BILL_ENABLE_DATE")
    private Date enableDate;

    @JSONField(name = "BILL_DISABLE_DATE")
    private Date disableDate;

    @JSONField(name = "BILL_LAST_UPDATE_DATE")
    private Date lastUpdDate;

    @JSONField(name = "LINE_ID")
    private String detailId;

    @JSONField(name = "BILL_MO_LOT_NO")
    private String moLotNo;

    private String detailStatus;

    @JSONField(name = "WKP_NO")
    private String wkpNo;

    @JSONField(name = "ITEM_ID_OLD")
    private String itemIdOld;

    @JSONField(name = "ITEM_ID_NEW")
    private String itemIdNew;

    @JSONField(name = "OPERATION_TYPE")
    private String operationType;

    @JSONField(name = "MO_LOT_NO")
    private String detailMoLotNo;

    @JSONField(name = "POS_NO")
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
                .setDisableDate(changeBillDTO.getDisableDate())
                .setLastUpdateDate(changeBillDTO.getLastUpdDate());
        if (StringUtils.isBlank(billBuildVO.getBillStatus())) {
            billBuildVO.setBillStatus(ChangeBillStatusEnum.ACTIVE.getCode());
        }
        return billBuildVO;
    }

    public static ChangeBillDetailBuildVO buildDetailVO(SourceChangeBillDTO changeBillDTO) {
        ChangeBillDetailBuildVO detailBuildVO = new ChangeBillDetailBuildVO();
        detailBuildVO.setBillId(changeBillDTO.getBillId())
                .setOrganizationId(changeBillDTO.getOrganizationId())
                .setDetailId(changeBillDTO.getDetailId())
                .setMoLotNo(changeBillDTO.getDetailMoLotNo())
                .setStatus(changeBillDTO.getDetailStatus())
                .setWkpNo(changeBillDTO.getWkpNo())
                .setItemIdOld(changeBillDTO.getItemIdOld())
                .setItemIdNew(changeBillDTO.getItemIdNew())
                .setOperationType(changeBillDTO.getOperationType())
                .setPosNo(changeBillDTO.getPosNo())
                .setEnableDate(changeBillDTO.getDetailEnableDate())
                .setDisableDate(changeBillDTO.getDetailDisableDate());
        if (StringUtils.isBlank(detailBuildVO.getStatus())) {
            detailBuildVO.setStatus(ChangeBillStatusEnum.ACTIVE.getCode());
        }
        if (StringUtils.isBlank(detailBuildVO.getWkpNo())) {
            detailBuildVO.setWkpNo("");
        }
        if (Objects.isNull(detailBuildVO.getEnableDate())) {
            detailBuildVO.setEnableDate(new Date());
        }
        return detailBuildVO;
    }

}
