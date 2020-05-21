package com.cvte.scm.wip.domain.core.requirement.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.changeorder.entity.ChangeOrderEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum;
import io.swagger.annotations.ApiModelProperty;
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

    @ApiModelProperty("执行类型, 新增/更新/撤销(删除)")
    private ChangedTypeEnum executeType;

    private List<ReqInstructionDetailBuildVO> detailList;

    public static ReqInstructionBuildVO buildVO(ChangeOrderEntity orderEntity) {
        ReqInstructionBuildVO instructionBuildVO = new ReqInstructionBuildVO();
        instructionBuildVO.setSourceChangeBillId(orderEntity.getBillId())
                .setEnableDate(orderEntity.getEnableDate())
                .setDisableDate(orderEntity.getDisableDate());
        return instructionBuildVO;
    }

}
