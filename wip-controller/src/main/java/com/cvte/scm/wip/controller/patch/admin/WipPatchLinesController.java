package com.cvte.scm.wip.controller.patch.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchEntity;
import com.cvte.scm.wip.domain.core.patch.service.WipPatchLinesService;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqMtrsEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqMtrsService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 3:54 下午
 */
@RestController
@RequestMapping("/admin/patch/lines")
@Api(tags = "补料单行信息")
@Slf4j
public class WipPatchLinesController {

    private WipReqMtrsService wipReqMtrsService;

    private WipPatchLinesService wipPatchLinesService;

    public WipPatchLinesController(WipReqMtrsService wipReqMtrsService, WipPatchLinesService wipPatchLinesService) {
        this.wipReqMtrsService = wipReqMtrsService;
        this.wipPatchLinesService = wipPatchLinesService;
    }

    @GetMapping("/mtrs/list/{headerId}/{itemId}")
    public RestResponse getWipPatchLines(@PathVariable("headerId") String headerId,
                                         @PathVariable("itemId") String itemId){
        List<WipReqMtrsEntity> allMtrs = wipReqMtrsService.getAllMtrs(headerId, itemId);
        return new RestResponse().setData(allMtrs);
    }

    @DeleteMapping("/delete/{billId}/{id}")
    public RestResponse deleteWipPatchLines(@PathVariable("id") Integer id,
                                            @PathVariable("billId") String billId){
        wipPatchLinesService.delete(billId,id);
        return new RestResponse();
    }

    @PostMapping("/save")
    public RestResponse deleteWipPatchLines(@RequestBody WipPatchEntity wipPatchEntity){
        wipPatchLinesService.saveList(wipPatchEntity.getLinesList(),wipPatchEntity);
        return new RestResponse();
    }
    @GetMapping("/verify/{billId}/{itemId}")
    public RestResponse verifyWipPatchLines(@PathVariable("billId") String billId,
                                            @PathVariable("itemId") String itemId){
        boolean b = wipPatchLinesService.verifyWipPatchLines(billId, itemId);
        return new RestResponse().setData(b);
    }

}
