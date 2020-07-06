package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.app.req.line.ReqLineReplaceApplication;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.common.utils.EntityUtils;
import com.cvte.scm.wip.domain.common.view.vo.SysViewPageParamVO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqMtrsEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLinePageService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqLineService;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqMtrsService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedModeEnum;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : jf
 * Date    : 2019.12.30
 * Time    : 15:02
 * Email   ：jiangfeng7128@cvte.com
 */
@Slf4j
@RestController
@Api(tags = "投料单行控制层")
@RequestMapping("/admin/req/lines")
public class WipReqLinesController {

    private WipReqLinePageService wipReqLinePageService;
    private WipReqMtrsService wipReqMtrsService;
    private WipReqLineService wipReqLineService;
    private ReqLineReplaceApplication reqLineReplaceApplication;

    public WipReqLinesController(WipReqLinePageService wipReqLinePageService, WipReqMtrsService wipReqMtrsService, WipReqLineService wipReqLineService, ReqLineReplaceApplication reqLineReplaceApplication) {
        this.wipReqLinePageService = wipReqLinePageService;
        this.wipReqMtrsService = wipReqMtrsService;
        this.wipReqLineService = wipReqLineService;
        this.reqLineReplaceApplication = reqLineReplaceApplication;
    }

    @PostMapping("/tree")
    public RestResponse tree(@RequestBody SysViewPageParamVO sysViewPageParam) {
        return new RestResponse().setData(wipReqLinePageService.tree(sysViewPageParam));
    }

    /**
     * 未发料数量汇总
     */
    @GetMapping("/sumUnissuedQty")
    public RestResponse sumUnissuedQty(@RequestParam("headerId") String headerId) {
        return new RestResponse().setData(wipReqLinePageService.sumUnissuedQty(headerId));
    }

    @PostMapping("/addOne")
    public RestResponse add(@RequestBody WipReqLineEntity wipReqLine) {
        wipReqLineService.addOne(wipReqLine, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true);
        return ResponseFactory.getOkResponse("投料单行数据新增成功！");
    }

    @PostMapping("/addMany")
    public RestResponse add(@RequestBody List<WipReqLineEntity> wipReqLine) {
        wipReqLineService.addMany(wipReqLine, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, EntityUtils.getWipUserId());
        return ResponseFactory.getOkResponse("投料单行数据新增成功！");
    }

    @PostMapping("/cancelByLineIds")
    public RestResponse cancel(@RequestBody String... lineIds) {
        wipReqLineService.cancelledByLineIds(ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, lineIds);
        return ResponseFactory.getOkResponse("投料单行数据删除成功！");
    }

    @PostMapping("/cancelByWipReqLines")
    public RestResponse cancel(@RequestBody List<WipReqLineEntity> wipReqLineList) {
        wipReqLineService.cancelledByConditions(wipReqLineList, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, EntityUtils.getWipUserId());
        return ResponseFactory.getOkResponse("投料单行数据删除成功！");
    }

    @PostMapping("/replace")
    public RestResponse replace(@RequestBody List<WipReqLineEntity> wipReqLinesList) {
        reqLineReplaceApplication.doAction(wipReqLinesList);
        return ResponseFactory.getOkResponse("投料单行数据替换成功！");
    }

    @PostMapping("/replaceWkp")
    public RestResponse replaceWkp(@RequestBody List<WipReqLineEntity> wipReqLinesList) {
        wipReqLineService.replaceWkp(wipReqLinesList, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, EntityUtils.getWipUserId());
        return ResponseFactory.getOkResponse("投料单行数据替换成功！");
    }

    @PostMapping("/update")
    public RestResponse update(@RequestBody List<WipReqLineEntity> wipReqLinesList) {
        wipReqLineService.update(wipReqLinesList, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, true, EntityUtils.getWipUserId());
        return ResponseFactory.getOkResponse("投料单行数据修改成功！");
    }

    @PostMapping("/preparation")
    public RestResponse prepare(@RequestBody WipReqLineEntity wipReqLine) {
        wipReqLineService.preparedByWipReqLines(wipReqLine, ExecutionModeEnum.STRICT, ChangedModeEnum.AUTOMATIC, false);
        return ResponseFactory.getOkResponse("投料单行数据修改成功！");
    }

    @PostMapping("/canSubstitute")
    public RestResponse canSubstitute(@RequestBody WipReqMtrsEntity wipReqMtrs) {
        return ResponseFactory.getOkResponse(wipReqMtrsService.canSubstitute(wipReqMtrs));
    }
}