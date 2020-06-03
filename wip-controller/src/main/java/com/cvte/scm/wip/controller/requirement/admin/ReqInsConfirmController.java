package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.req.confirm.ReqInsConfirmApplication;
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
public class ReqInsConfirmController {

    private ReqInsConfirmApplication reqInsConfirmApplication;

    public ReqInsConfirmController(ReqInsConfirmApplication reqInsConfirmApplication) {
        this.reqInsConfirmApplication = reqInsConfirmApplication;
    }

    @PostMapping("/confirm")
    public RestResponse confirm(@RequestBody String... insHeaderId) {
        reqInsConfirmApplication.doAction(insHeaderId);
        return new RestResponse();
    }

}
