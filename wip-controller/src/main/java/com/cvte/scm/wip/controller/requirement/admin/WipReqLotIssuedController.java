package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.req.lot.ReqLotIssuedDeleteApplication;
import com.cvte.scm.wip.app.req.lot.ReqLotIssuedLockApplication;
import com.cvte.scm.wip.app.req.lot.ReqLotIssuedSaveApplication;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/1/17 14:32
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@Validated
@Api(tags = "投料单领料批次接口")
@RestController
@RequestMapping("/admin/req/lot")
public class WipReqLotIssuedController {

    private ReqLotIssuedSaveApplication reqLotIssuedSaveApplication;
    private ReqLotIssuedDeleteApplication reqLotIssuedDeleteApplication;
    private ReqLotIssuedLockApplication reqLotIssuedLockApplication;

    public WipReqLotIssuedController(ReqLotIssuedSaveApplication reqLotIssuedSaveApplication, ReqLotIssuedDeleteApplication reqLotIssuedDeleteApplication, ReqLotIssuedLockApplication reqLotIssuedLockApplication) {
        this.reqLotIssuedSaveApplication = reqLotIssuedSaveApplication;
        this.reqLotIssuedDeleteApplication = reqLotIssuedDeleteApplication;
        this.reqLotIssuedLockApplication = reqLotIssuedLockApplication;
    }

    @PostMapping("/save")
    public RestResponse save(@Valid @RequestBody WipReqLotIssuedEntity wipReqLotIssued) {
        reqLotIssuedSaveApplication.doAction(wipReqLotIssued);
        return new RestResponse();
    }

    @DeleteMapping("/invalid/{idStr}")
    public RestResponse invalid(@PathVariable("idStr") String idStr) {
        reqLotIssuedDeleteApplication.doAction(idStr);
        return new RestResponse();
    }

    @PostMapping("/lock")
    public RestResponse lock(@RequestBody String[] idArr) {
        reqLotIssuedLockApplication.doAction(idArr);
        return new RestResponse();
    }

}
