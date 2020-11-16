package com.cvte.scm.wip.app.rtc.save;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.rtc.entity.WipMtrRtcHeaderEntity;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/21 15:40
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
@Transactional(transactionManager = "pgTransactionManager")
public class WipMtrRtcHeaderSaveApplication {

    public void doAction(WipMtrRtcHeaderBuildVO headerBuildVO) {
        if (StringUtils.isBlank(headerBuildVO.getHeaderId())) {
            throw new ParamsIncorrectException("单据ID不可为空");
        }
        WipMtrRtcHeaderEntity rtcHeader = WipMtrRtcHeaderEntity.get().getById(headerBuildVO.getHeaderId());
        if (Objects.isNull(rtcHeader)) {
            throw new ParamsIncorrectException("找不到该单据");
        }
        WipMtrRtcHeaderBuildVO specifiedHeaderBuildVO = new WipMtrRtcHeaderBuildVO();
        specifiedHeaderBuildVO.setRemark(headerBuildVO.getRemark());
        rtcHeader.update(specifiedHeaderBuildVO);
        rtcHeader.save(false);
    }

}
