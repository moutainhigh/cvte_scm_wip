package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.app.req.line.ReqLineSyncApplication;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqHeaderEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqHeaderPageService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqHeaderService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @author : xueyuting
 * @version : 1.0
 * date    : 2019/12/30 15:02
 * email   : xueyuting@cvte.com tingyx96@qq.com
 */
@Slf4j
@RestController
@Api(tags = "投料单头接口")
@RequestMapping("/admin/req/header")
public class WipReqHeaderController {

    private WipReqHeaderService wipReqHeaderService;
    private WipReqHeaderPageService wipReqHeaderPageService;
    private ReqLineSyncApplication reqLineSyncApplication;

    public WipReqHeaderController(WipReqHeaderService wipReqHeaderService, WipReqHeaderPageService wipReqHeaderPageService, ReqLineSyncApplication reqLineSyncApplication) {
        this.wipReqHeaderService = wipReqHeaderService;
        this.wipReqHeaderPageService = wipReqHeaderPageService;
        this.reqLineSyncApplication = reqLineSyncApplication;
    }

    @PostMapping("/update")
    public RestResponse cancel(@RequestBody List<WipReqHeaderEntity> wipReqHeaderList) {
        wipReqHeaderService.updateWipReqHeaders(wipReqHeaderList, ExecutionModeEnum.STRICT);
        return ResponseFactory.getOkResponse("投料单行数据删除成功！");
    }

    @GetMapping("/detail/{headerId}")
    public RestResponse headerInfo(@PathVariable("headerId") String headerId) {
        WipReqHeaderEntity reqHeader = wipReqHeaderPageService.getDetail(headerId);
        return new RestResponse().setData(reqHeader);
    }

    @PostMapping("/sync")
    public RestResponse sync(@RequestBody Map<String, Object> map) {
        reqLineSyncApplication.doAction(map);
        return new RestResponse();
    }

}
