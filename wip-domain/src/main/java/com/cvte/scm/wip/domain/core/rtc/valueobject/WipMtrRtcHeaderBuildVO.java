package com.cvte.scm.wip.domain.core.rtc.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/8 17:39
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipMtrRtcHeaderBuildVO implements VO {

    private String headerId;

    private String organizationId;

    private String moNo;

    private String wkpNo;

    private String billType;

    private BigDecimal billQty;

    private String invpNo;

    private String deptNo;

    private String remark;

    private String sourceBillNo;

    private List<String> itemList;

    private String factoryId;

    private String moId;

    private BigDecimal originQty;

    private BigDecimal completeQty;

    private BigDecimal unCompleteQty;


}
