package com.cvte.scm.wip.domain.core.requirement.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 16:20
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ReqInstructionBuildVO implements VO {

    private String instructionHeaderId;

    private String sourceChangeBillId;

    private String instructionHeaderStatus;

    private String aimHeaderId;

    private String aimReqLotNo;

    private Date enableDate;

    private Date disableDate;

    private List<ReqInstructionDetailBuildVO> detailList;

    public static ReqInstructionBuildVO buildVO(ChangeBillEntity billEntity) {
        ReqInstructionBuildVO instructionBuildVO = new ReqInstructionBuildVO();
        instructionBuildVO.setSourceChangeBillId(billEntity.getBillId())
                .setEnableDate(billEntity.getEnableDate())
                .setDisableDate(billEntity.getDisableDate());
        return instructionBuildVO;
    }

}
