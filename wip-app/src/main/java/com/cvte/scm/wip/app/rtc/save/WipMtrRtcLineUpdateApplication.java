package com.cvte.scm.wip.app.rtc.save;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.common.enums.ExecutionResultEnum;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcLineService;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrInvQtyCheckVO;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/10 19:01
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcLineUpdateApplication {

    private WipMtrRtcLineService wipMtrRtcLineService;

    public WipMtrRtcLineUpdateApplication(WipMtrRtcLineService wipMtrRtcLineService) {
        this.wipMtrRtcLineService = wipMtrRtcLineService;
    }

    public RestResponse doAction(List<WipMtrRtcLineBuildVO> rtcLineBuildVOList) {
        RestResponse restResponse = new RestResponse();
        List<WipMtrInvQtyCheckVO> invQtyCheckVOS = wipMtrRtcLineService.batchUpdate(rtcLineBuildVOList);
        if (ListUtil.notEmpty(invQtyCheckVOS)) {
            restResponse.setStatus("4000002");
            boolean failed = invQtyCheckVOS.stream().map(WipMtrInvQtyCheckVO::getNoticeType).anyMatch(type -> type.equals(ExecutionResultEnum.FAILED.getCode()));
            if (failed) {
                restResponse.setMessage("保存失败");
            } else {
                restResponse.setMessage("保存成功");
            }
            restResponse.setData(invQtyCheckVOS);
            return restResponse;
        }
        return restResponse;
    }

}
