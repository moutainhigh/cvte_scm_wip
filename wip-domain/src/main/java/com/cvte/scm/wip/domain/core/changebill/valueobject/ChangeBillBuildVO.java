package com.cvte.scm.wip.domain.core.changebill.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

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
@Accessors(chain = true)
public class ChangeBillBuildVO implements VO {

    private String billId;

    private String billNo;

    private String organizationId;

    private String billType;

    private String moId;

    private String billStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enableDate;

    private Date disableDate;

    private Date lastUpdDate;

    private String changeContent;
    
    private String remarks;

    private String pcRemarks;

    private String motLotNo;

    private String sourceNo;

    private List<ChangeBillDetailBuildVO> detailVOList;

}
