package com.cvte.scm.wip.domain.core.requirement.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
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

    private BigDecimal itemQty;

    private String operationType;

    private String insStatus;

    private Date enableDate;

    private Date disableDate;

    private Date confirmDate;

    private String confirmedBy;

    private String invalidBy;

    private String invalidReason;

}
