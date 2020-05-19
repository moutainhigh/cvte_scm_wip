package com.cvte.scm.wip.domain.core.changeorder.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 14:22
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class ChangeOrderBuildVO implements VO {

    private String billId;

    private String billNo;

    private String organizationId;

    private String billType;

    private String moId;

    private String billStatus;

    private Date enableDate;

    private Date disableDate;

    private List<ChangeOrderDetailBuildVO> detailVOList;

}
