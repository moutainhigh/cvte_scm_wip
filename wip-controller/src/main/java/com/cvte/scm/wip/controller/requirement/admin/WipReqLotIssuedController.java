package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.req.lot.ReqLotIssuedLockApplication;
import com.cvte.scm.wip.app.req.lot.ReqLotIssuedSaveApplication;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotIssuedService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private ReqLotIssuedLockApplication reqLotIssuedLockApplication;
    private WipReqLotIssuedService wipReqLotIssuedService;

    public WipReqLotIssuedController(ReqLotIssuedSaveApplication reqLotIssuedSaveApplication, ReqLotIssuedLockApplication reqLotIssuedLockApplication, WipReqLotIssuedService wipReqLotIssuedService) {
        this.reqLotIssuedSaveApplication = reqLotIssuedSaveApplication;
        this.reqLotIssuedLockApplication = reqLotIssuedLockApplication;
        this.wipReqLotIssuedService = wipReqLotIssuedService;
    }

    @PostMapping("/save_all")
    public RestResponse saveAll(@RequestBody List<WipReqLotIssuedEntity> itemLotIssuedList) {
        reqLotIssuedSaveApplication.doAction(itemLotIssuedList);
        return new RestResponse();
    }

    @PostMapping("/lock")
    public RestResponse lock(@RequestBody String[] idArr) {
        reqLotIssuedLockApplication.doAction(idArr);
        return new RestResponse();
    }

    @PostMapping("/delete")
    public RestResponse delete(@RequestBody WipReqLotIssuedEntity reqLotIssued) {
        wipReqLotIssuedService.delete(reqLotIssued);
        return new RestResponse();
    }

}
