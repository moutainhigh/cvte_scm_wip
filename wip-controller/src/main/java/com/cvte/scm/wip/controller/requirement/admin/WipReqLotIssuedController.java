package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotIssuedService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
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

    private WipReqLotIssuedService wipReqLotIssuedService;

    public WipReqLotIssuedController(WipReqLotIssuedService wipReqLotIssuedService) {
        this.wipReqLotIssuedService = wipReqLotIssuedService;
    }

    @PostMapping("/save")
    public RestResponse save(@Valid @RequestBody WipReqLotIssuedEntity wipReqLotIssued) {
        wipReqLotIssuedService.add(wipReqLotIssued);
        return new RestResponse();
    }

    @DeleteMapping("/invalid/{idStr}")
    public RestResponse invalid(@PathVariable("idStr") String idStr) {
        List<String> idList = Arrays.asList(idStr.split(","));
        wipReqLotIssuedService.invalid(idList);
        return new RestResponse();
    }
}
