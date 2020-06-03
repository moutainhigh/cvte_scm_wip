package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.req.confirm.ReqInsConfirmApplication;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLinePageService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/2 11:16
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@RestController
@Api(tags = "待确认列表")
@RequestMapping("/admin/req/ins")
public class ReqInsController {

    private ReqInsConfirmApplication reqInsConfirmApplication;
    private WipReqLinePageService wipReqLinePageService;

    public ReqInsController(ReqInsConfirmApplication reqInsConfirmApplication, WipReqLinePageService wipReqLinePageService) {
        this.reqInsConfirmApplication = reqInsConfirmApplication;
        this.wipReqLinePageService = wipReqLinePageService;
    }

    @PostMapping("/confirm")
    public RestResponse confirm(@RequestBody String... insHeaderId) {
        reqInsConfirmApplication.doAction(insHeaderId);
        return new RestResponse();
    }

    @PostMapping("/info")
    public RestResponse reqInsInfo(@RequestBody SysViewPageParamVO sysViewPageParam) {
        return new RestResponse().setData(wipReqLinePageService.reqInsInfo(sysViewPageParam));
    }

}
