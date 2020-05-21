package com.cvte.scm.wip.domain.core.changeorder.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

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
public class ChangeReqLineVO implements VO {

    private String lineId;

    private String headerId;

    private String organizationId;

    private String itemId;

    private String itemNo;

    private String lotNumber;

    @ApiModelProperty(value = "工序号")
    private String wkpNo;

    @ApiModelProperty(value = "位号")
    private String posNo;

    private BigDecimal unitQty;

    private BigDecimal reqQty;

    private BigDecimal issuedQty;

    private String lineStatus;

}
