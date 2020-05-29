package com.cvte.scm.wip.spi.subrule.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/29 10:37
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class SubRuleExecuteLogDTO {

    private String ruleNo;

    private List<SuccessLogDTO> successRecordList;

    private List<FailureLogDTO> failureRecordList;

    @Data
    public class SuccessLogDTO {

        private String orderNo;

        private String beforeItemNo;

        private String afterItemNo;

        private Date crtTime;

    }

    @Data
    public class FailureLogDTO {

        private String orderNo;

        private String failureReason;

        private Date crtTime;

    }

}
