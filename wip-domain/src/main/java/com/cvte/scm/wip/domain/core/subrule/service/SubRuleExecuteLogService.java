package com.cvte.scm.wip.domain.core.subrule.service;

import com.cvte.scm.wip.domain.core.subrule.valueobject.SubRuleExeLogVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/29 09:36
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface SubRuleExecuteLogService {

    List<SubRuleExeLogVO> getLogByRuleNo(String ruleNo);

}
