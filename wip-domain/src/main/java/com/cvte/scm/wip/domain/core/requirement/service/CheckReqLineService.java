package com.cvte.scm.wip.domain.core.requirement.service;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqItemVO;
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipReqLineKeyQueryVO;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/25 15:06
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class CheckReqLineService {

    private WipReqItemService wipReqItemService;

    public CheckReqLineService(WipReqItemService wipReqItemService) {
        this.wipReqItemService = wipReqItemService;
    }

    public void checkItemExists(String organizationId, String headerId, String wkpNo, String itemkey) {
        WipReqLineKeyQueryVO reqLineKeyQueryVO = WipReqLineKeyQueryVO.buildForReqItem(organizationId, headerId, wkpNo, Collections.singletonList(itemkey));
        List<WipReqItemVO> reqItemVOList = wipReqItemService.getReqItem(reqLineKeyQueryVO);
        if (ListUtil.empty(reqItemVOList)) {
            throw new ParamsIncorrectException(String.format("工单%s工序%s不存在投料%s", headerId, wkpNo, itemkey));
        }
    }

}
