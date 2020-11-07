package com.cvte.scm.wip.controller.requirement.api;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLotProcessService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private WipReqLotProcessService wipReqLotProcessService;

    public WipReqLotApiController(WipReqLotProcessService wipReqLotProcessService) {
        this.wipReqLotProcessService = wipReqLotProcessService;
    }

    @PostMapping("/lock")
    public RestResponse changeLockStatus(@RequestBody List<WipReqLotProcessEntity> wipReqLotProcessList) {
        wipReqLotProcessService.createAndProcess(wipReqLotProcessList);
        return new RestResponse();
    }

    @PostMapping("/process")
    public RestResponse process() {
        String msg = wipReqLotProcessService.getAndProcess();
        Map<String, String> rtnMap = new HashMap<>();
        rtnMap.put("code", "520"); // 处理完毕
        rtnMap.put("info", msg); // 处理完毕
        return new RestResponse().setData(rtnMap);
    }

}
