package com.cvte.scm.wip.controller.rework.api;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.core.rework.service.WipReworkBillHeaderService;
import com.cvte.scm.wip.domain.core.rework.service.WipReworkBillSyncService;
import com.cvte.scm.wip.domain.core.rework.valueobject.ApiRwkBillVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/31 10:14
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Api(tags = "投料单变更接口")
@RestController
@RequestMapping("/api/mo/rework")
@Slf4j
public class ApiRwkBillController {

    private WipReworkBillHeaderService rwkBillHService;
    private WipReworkBillSyncService wipReworkBillSyncService;

    public ApiRwkBillController(WipReworkBillHeaderService rwkBillHService, WipReworkBillSyncService wipReworkBillSyncService) {
        this.rwkBillHService = rwkBillHService;
        this.wipReworkBillSyncService = wipReworkBillSyncService;
    }

    @PostMapping("/billList")
    public RestResponse getBillList(@RequestBody ApiRwkBillVO apiRwkBillDTO) {
        return new RestResponse().setData(rwkBillHService.getBillList(apiRwkBillDTO));
    }

    @PostMapping("/syncToEbs")
    public RestResponse syncToEbs(@RequestBody List<String> billNoList) {
        wipReworkBillSyncService.syncBillToEbs(billNoList);
        return new RestResponse();
    }
}
