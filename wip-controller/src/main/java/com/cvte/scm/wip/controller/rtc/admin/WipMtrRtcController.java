package com.cvte.scm.wip.controller.rtc.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.rtc.refresh.WipMtrRtcRefreshApplication;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/9 19:01
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@RestController
@RequestMapping("/admin/rtc/header")
public class WipMtrRtcController {

    private WipMtrRtcRefreshApplication wipMtrRtcRefreshApplication;

    public WipMtrRtcController(WipMtrRtcRefreshApplication wipMtrRtcRefreshApplication) {
        this.wipMtrRtcRefreshApplication = wipMtrRtcRefreshApplication;
    }

    @PostMapping("/refresh")
    public RestResponse create(@RequestBody WipMtrRtcHeaderBuildVO rtcHeaderBuildVO) {
        return new RestResponse().setData(wipMtrRtcRefreshApplication.doAction(rtcHeaderBuildVO));
    }

}
