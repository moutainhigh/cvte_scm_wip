package com.cvte.scm.wip.domain.core.rework.service;

import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillHeaderEntity;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillLineEntity;
import com.cvte.scm.wip.domain.core.rework.valueobject.EbsReworkBillQueryVO;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 10:32
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
public interface EbsReworkBillHeaderService {

    List<EbsReworkBillQueryVO> getEbsRwkBillH(Map<String, String> paramMap);

    String syncToEbs(WipReworkBillHeaderEntity billHeader, List<WipReworkBillLineEntity> billLineList);

}
