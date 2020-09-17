package com.cvte.scm.wip.controller.rtc.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.rtc.service.WipMtrRtcViewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/17 19:24
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@RestController
@RequestMapping("/admin/rtc/view")
public class WipMtrRtcViewController {

    private WipMtrRtcViewService wipMtrRtcViewService;

    public WipMtrRtcViewController(WipMtrRtcViewService wipMtrRtcViewService) {
        this.wipMtrRtcViewService = wipMtrRtcViewService;
    }

    @PostMapping("/line")
    public RestResponse create(@RequestBody SysViewPageParamVO sysViewPageParam) {
        return new RestResponse().setData(wipMtrRtcViewService.lineView(sysViewPageParam));
    }

}
