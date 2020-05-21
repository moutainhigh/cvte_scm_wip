package com.cvte.scm.wip.domain.core.changeorder.service;

import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderBuildVO;
import com.cvte.scm.wip.domain.core.changeorder.valueobject.ChangeOrderQueryVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 11:19
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface SourceChangeOrderService {

    List<ChangeOrderBuildVO> querySourceChangeOrder(ChangeOrderQueryVO queryVO);

}
