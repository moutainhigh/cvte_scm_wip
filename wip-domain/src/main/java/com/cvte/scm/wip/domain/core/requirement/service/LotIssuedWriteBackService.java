package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.LotIssuedOpTypeEnum;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/7/28 09:56
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface LotIssuedWriteBackService {

    void writeBack(LotIssuedOpTypeEnum opType, WipReqLotIssuedEntity reqLotIssued);

}
