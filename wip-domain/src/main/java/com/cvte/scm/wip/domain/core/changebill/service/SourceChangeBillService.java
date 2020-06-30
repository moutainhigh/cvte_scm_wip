package com.cvte.scm.wip.domain.core.changebill.service;

import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/21 11:19
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface SourceChangeBillService {

    List<ChangeBillBuildVO> querySourceChangeBill(ChangeBillQueryVO queryVO);

}
