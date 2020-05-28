package com.cvte.scm.wip.domain.core.requirement.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillDetailEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 16:26
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ReqInsDetailBuildVO implements VO {

    private String insDetailId;

    private String insHeaderId;

    private String organizationId;

    private String sourceChangeDetailId;

    private String moLotNo;

    private String itemIdOld;

    private String itemIdNew;

    private String wkpNo;

    private String posNo;

    private BigDecimal itemQty;

    private BigDecimal itemUnitQty;

    private String operationType;

    private String insStatus;

    private Date enableDate;

    private Date disableDate;

    public static ReqInsDetailBuildVO buildVO(ChangeBillDetailEntity billDetailEntity) {
        ReqInsDetailBuildVO detailBuildVO = new ReqInsDetailBuildVO();
        detailBuildVO.setOrganizationId(billDetailEntity.getOrganizationId())
                .setSourceChangeDetailId(billDetailEntity.getDetailId())
                .setMoLotNo(billDetailEntity.getMoLotNo())
                .setPosNo(billDetailEntity.getPosNo())
                .setItemIdOld(billDetailEntity.getItemIdOld())
                .setItemIdNew(billDetailEntity.getItemIdNew())
                .setWkpNo(billDetailEntity.getWkpNo())
                .setItemQty(billDetailEntity.getItemQty())
                .setEnableDate(billDetailEntity.getEnableDate())
                .setDisableDate(billDetailEntity.getDisableDate());
        return detailBuildVO;
    }

    public static List<ReqInsDetailBuildVO> batchBuildVO(List<ChangeBillDetailEntity> detailEntityList) {
        List<ReqInsDetailBuildVO> detailBuildVOList = new ArrayList<>();
        for (ChangeBillDetailEntity detailEntity : detailEntityList) {
            detailBuildVOList.add(ReqInsDetailBuildVO.buildVO(detailEntity));
        }
        return detailBuildVOList;
    }

}
