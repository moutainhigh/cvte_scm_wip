package com.cvte.scm.wip.domain.core.requirement.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Collections;
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
public class ReqInsBuildVO implements VO {

    private String insHeaderId;

    private String sourceChangeBillId;

    private String insHeaderStatus;

    private String aimHeaderId;

    private String aimReqLotNo;

    private Date enableDate;

    private Date disableDate;

    private String changeType;

    private String invalidBy;

    private String invalidReason;

    private List<ReqInsDetailBuildVO> detailList = Collections.emptyList();

    public static ReqInsBuildVO buildVO(ChangeBillEntity billEntity) {
        ReqInsBuildVO insBuildVO = new ReqInsBuildVO();
        insBuildVO.setSourceChangeBillId(billEntity.getBillId())
                .setEnableDate(billEntity.getEnableDate())
                .setDisableDate(billEntity.getDisableDate());
        return insBuildVO;
    }

}
