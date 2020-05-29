package com.cvte.scm.wip.domain.core.subrule.valueobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/29 09:49
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class SubRuleExeLogVO {

    private String ruleNo;

    private String orderNo;

    private String beforeItemNo;

    private String afterItemNo;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date crtTime;

    private String result;

}
