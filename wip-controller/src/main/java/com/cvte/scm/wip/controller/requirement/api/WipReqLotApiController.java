package com.cvte.scm.wip.controller.requirement.api;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.req.lot.ReqLotProcessApplication;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/4 09:19
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Validated
@Api(tags = "投料单领料批次接口")
@RestController
@RequestMapping("/api/req/lot")
public class WipReqLotApiController {

    private ReqLotProcessApplication reqLotProcessApplication;

    public WipReqLotApiController(ReqLotProcessApplication reqLotProcessApplication) {
        this.reqLotProcessApplication = reqLotProcessApplication;
    }

    @PostMapping("/lock")
    public RestResponse changeLockStatus(@RequestBody List<WipReqLotProcessEntity> wipReqLotProcessList) {
        reqLotProcessApplication.doAction(wipReqLotProcessList);
        return new RestResponse();
    }

}
