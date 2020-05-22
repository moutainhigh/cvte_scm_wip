package com.cvte.scm.wip.domain.core.changebill.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 14:24
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ChangeBillDetailBuildVO implements VO {

    private String detailId;

    private String billId;

    private String moLotNo;

    private String detailStatus;

    private String organizationId;

    private String wkpNo;

    private String itemIdOld;

    private String itemIdNew;

    private String operationType;

    private String posNo;

    private BigDecimal itemQty;

    private BigDecimal itemUnitQty;

    private Date enableDate;

    private Date disableDate;

}
