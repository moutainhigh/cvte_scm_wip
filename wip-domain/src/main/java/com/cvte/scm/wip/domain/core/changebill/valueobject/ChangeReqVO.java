package com.cvte.scm.wip.domain.core.changebill.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/20 20:36
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ChangeReqVO implements VO {

    private String headerId;

    private String organizationId;

    @ApiModelProperty(value = "来源批次号")
    private String sourceLotNo;

    private String billStatus;

    private BigDecimal billQty;

    private List<ChangeReqLineVO> reqLineVOList;

    public static ChangeReqVO buildVO(WipReqHeaderEntity headerEntity) {
        ChangeReqVO reqVO = new ChangeReqVO();
        reqVO.setHeaderId(headerEntity.getHeaderId())
                .setOrganizationId(headerEntity.getOrganizationId())
                .setSourceLotNo(headerEntity.getSourceLotNo())
                .setBillStatus(headerEntity.getBillStatus())
                .setBillQty(new BigDecimal(headerEntity.getBillQty()));
        return reqVO;
    }

}
