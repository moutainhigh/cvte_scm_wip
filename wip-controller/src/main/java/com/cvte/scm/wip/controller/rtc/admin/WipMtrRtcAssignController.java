package com.cvte.scm.wip.controller.rtc.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.rtc.save.WipMtrRtcAssignSaveApplication;
import com.cvte.scm.wip.domain.core.rtc.valueobject.WipMtrRtcAssignBuildVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/11 09:54
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@RestController
@RequestMapping("/admin/rtc/assign")
public class WipMtrRtcAssignController {

    private WipMtrRtcAssignSaveApplication wipMtrRtcAssignSaveApplication;

    public WipMtrRtcAssignController(WipMtrRtcAssignSaveApplication wipMtrRtcAssignSaveApplication) {
        this.wipMtrRtcAssignSaveApplication = wipMtrRtcAssignSaveApplication;
    }

    @PostMapping("/batch_save")
    public RestResponse create(@RequestBody List<WipMtrRtcAssignBuildVO> rtcAssignBuildVOList) {
        return new RestResponse().setData(wipMtrRtcAssignSaveApplication.doAction(rtcAssignBuildVOList));
    }

}
