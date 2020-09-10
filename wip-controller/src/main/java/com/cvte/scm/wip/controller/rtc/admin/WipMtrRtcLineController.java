package com.cvte.scm.wip.controller.rtc.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.rtc.update.WipMtrRtcLineUpdateApplication;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcLineBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/10 19:02
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@RestController
@RequestMapping("/admin/rtc/line")
public class WipMtrRtcLineController {

    private WipMtrRtcLineUpdateApplication wipMtrRtcLineUpdateApplication;

    public WipMtrRtcLineController(WipMtrRtcLineUpdateApplication wipMtrRtcLineUpdateApplication) {
        this.wipMtrRtcLineUpdateApplication = wipMtrRtcLineUpdateApplication;
    }

    @PostMapping("/batch_update")
    public RestResponse create(@RequestBody List<WipMtrRtcLineBuildVO> rtcLineBuildVOList) {
        return new RestResponse().setData(wipMtrRtcLineUpdateApplication.doAction(rtcLineBuildVOList));
    }

}
