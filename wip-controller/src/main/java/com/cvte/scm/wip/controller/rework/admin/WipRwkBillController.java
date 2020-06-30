package com.cvte.scm.wip.controller.rework.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.core.rework.service.WipReworkBillHeaderService;
import com.cvte.scm.wip.domain.core.rework.service.WipReworkBillSyncService;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkBillHVO;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkMoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/23 18:31
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@RestController
@RequestMapping("/admin/mo/rework")
public class WipRwkBillController {

    private WipReworkBillHeaderService wipReworkBillHeaderService;
    private WipReworkBillSyncService wipReworkBillSyncService;

    public WipRwkBillController(WipReworkBillHeaderService wipReworkBillHeaderService, WipReworkBillSyncService wipReworkBillSyncService) {
        this.wipReworkBillHeaderService = wipReworkBillHeaderService;
        this.wipReworkBillSyncService = wipReworkBillSyncService;
    }

    @PostMapping("/create")
    public RestResponse create(@RequestBody WipRwkBillHVO moReworkBillDTO) {
        return new RestResponse().setData(wipReworkBillHeaderService.batchCreateBill(moReworkBillDTO));
    }

    @DeleteMapping("/invalid/{billIds}")
    public RestResponse batchInvalid(@PathVariable("billIds")String lineIds) {
        List<String> lineIdList = Arrays.asList(lineIds.split(","));
        wipReworkBillHeaderService.batchInvalidBill(lineIdList);
        return new RestResponse();
    }

    @PatchMapping("/update")
    public RestResponse update(@RequestBody WipRwkBillHVO reworkBill) {
        wipReworkBillHeaderService.updateBill(reworkBill);
        return new RestResponse();
    }

    @PostMapping("/getMoLotList")
    public RestResponse getMoLotList(@RequestBody WipRwkMoVO moParam) {
        moParam.setNeedPage(true);
        return new RestResponse().setData(wipReworkBillHeaderService.getMoLotList(moParam));
    }

    @GetMapping("/billDetail/{billNo}")
    public RestResponse getBillDetail(@PathVariable("billNo") String billNo) {
        return new RestResponse().setData(wipReworkBillHeaderService.getBillDetail(billNo));
    }

    @PostMapping("/syncToEbs")
    public RestResponse syncToEbs(@RequestBody List<String> billNoList) {
        return new RestResponse().setData(wipReworkBillSyncService.syncBillToEbs(billNoList));
    }

}
