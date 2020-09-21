package com.cvte.scm.wip.controller.rtc.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.rtc.refresh.WipMtrRtcRefreshApplication;
import com.cvte.scm.wip.app.rtc.status.*;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcHeaderBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @author : xueyuting
 * @version : 1.0
 * email   : xueyuting@cvte.com
 * @since : 2020/9/9 19:01
 */
@Slf4j
@RestController
@RequestMapping("/admin/rtc/header")
public class WipMtrRtcController {

    private WipMtrRtcRefreshApplication wipMtrRtcRefreshApplication;
    private WipMtrRtcHeaderSubmitApplication wipMtrRtcHeaderSubmitApplication;
    private WipMtrRtcHeaderReviewApplication wipMtrRtcHeaderReviewApplication;
    private WipMtrRtcHeaderCloseApplication wipMtrRtcHeaderCloseApplication;
    private WipMtrRtcHeaderCancelApplication wipMtrRtcHeaderCancelApplication;

    public WipMtrRtcController(WipMtrRtcRefreshApplication wipMtrRtcRefreshApplication, WipMtrRtcHeaderSubmitApplication wipMtrRtcHeaderSubmitApplication, WipMtrRtcHeaderReviewApplication wipMtrRtcHeaderReviewApplication, WipMtrRtcHeaderCloseApplication wipMtrRtcHeaderCloseApplication, WipMtrRtcHeaderCancelApplication wipMtrRtcHeaderCancelApplication) {
        this.wipMtrRtcRefreshApplication = wipMtrRtcRefreshApplication;
        this.wipMtrRtcHeaderSubmitApplication = wipMtrRtcHeaderSubmitApplication;
        this.wipMtrRtcHeaderReviewApplication = wipMtrRtcHeaderReviewApplication;
        this.wipMtrRtcHeaderCloseApplication = wipMtrRtcHeaderCloseApplication;
        this.wipMtrRtcHeaderCancelApplication = wipMtrRtcHeaderCancelApplication;
    }

    @PostMapping("/refresh")
    public RestResponse create(@RequestBody WipMtrRtcHeaderBuildVO rtcHeaderBuildVO) {
        return new RestResponse().setData(wipMtrRtcRefreshApplication.doAction(rtcHeaderBuildVO));
    }

    @PostMapping("/submit/{headerId}")
    public RestResponse submit(@PathVariable("headerId") String headerId) {
        wipMtrRtcHeaderSubmitApplication.doAction(headerId);
        return new RestResponse();
    }

    @PostMapping("/review")
    public RestResponse review(@RequestBody WipMtrRtcHeaderReviewDTO wipMtrRtcHeaderReviewDTO) {
        wipMtrRtcHeaderReviewApplication.doAction(wipMtrRtcHeaderReviewDTO);
        return new RestResponse();
    }

    @PostMapping("/close/{headerId}")
    public RestResponse close(@PathVariable("headerId") String headerId) {
        wipMtrRtcHeaderCloseApplication.doAction(headerId);
        return new RestResponse();
    }

    @PostMapping("/cancel/{headerId}")
    public RestResponse cancel(@PathVariable("headerId") String headerId) {
        wipMtrRtcHeaderCancelApplication.doAction(headerId);
        return new RestResponse();
    }

}
