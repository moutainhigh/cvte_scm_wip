package com.cvte.scm.wip.domain.core.changebill.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillDetailEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

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

    private String status;

    private String organizationId;

    private String wkpNo;

    private String itemIdOld;

    private String itemIdNew;

    private String operationType;

    private String posNo;

    private BigDecimal itemQty;

    private BigDecimal itemUnitQty;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enableDate;

    private Date disableDate;

    private String sourceLineId;

}
