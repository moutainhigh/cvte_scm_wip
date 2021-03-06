package com.cvte.scm.wip.controller.rtc.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.app.rtc.status.WipMtrRtcLineCancelApplication;
import com.cvte.scm.wip.app.rtc.save.WipMtrRtcLineUpdateApplication;
import com.cvte.scm.wip.app.rtc.status.WipMtrRtcPostApplication;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrInvQtyCheckVO;
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
    private WipMtrRtcLineCancelApplication wipMtrRtcLineCancelApplication;
    private WipMtrRtcPostApplication wipMtrRtcPostApplication;

    public WipMtrRtcLineController(WipMtrRtcLineUpdateApplication wipMtrRtcLineUpdateApplication, WipMtrRtcLineCancelApplication wipMtrRtcLineCancelApplication, WipMtrRtcPostApplication wipMtrRtcPostApplication) {
        this.wipMtrRtcLineUpdateApplication = wipMtrRtcLineUpdateApplication;
        this.wipMtrRtcLineCancelApplication = wipMtrRtcLineCancelApplication;
        this.wipMtrRtcPostApplication = wipMtrRtcPostApplication;
    }

    @PostMapping("/batch_update")
    public RestResponse create(@RequestBody List<WipMtrRtcLineBuildVO> rtcLineBuildVOList) {
        return wipMtrRtcLineUpdateApplication.doAction(rtcLineBuildVOList);
    }

    @PostMapping("/batch_cancel")
    public RestResponse batchCancel(@RequestBody String[] lineIds) {
        wipMtrRtcLineCancelApplication.doAction(lineIds);
        return new RestResponse();
    }

    @PostMapping("/post")
    public RestResponse batchPost(@RequestBody String[] lineIds) {
        wipMtrRtcPostApplication.doAction(lineIds);
        return new RestResponse();
    }

}
