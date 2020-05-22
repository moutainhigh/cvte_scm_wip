package com.cvte.scm.wip.controller.ckd.api;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.core.ckd.dto.*;
import com.cvte.scm.wip.domain.core.ckd.dto.query.WipMcLineStatusQuery;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskLineService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcWfService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author zy
 * @date 2020-04-28 16:19
 **/
@Api(tags = "配料任务第三方调用接口")
@RestController
@RequestMapping("/api/mc/task")
@Slf4j
public class WipMcTaskApiController {

    @Autowired
    private WipMcTaskService wipMcTaskService;

    @Autowired
    private WipMcTaskLineService wipMcTaskLineService;

    @Autowired
    private WipMcWfService wipMcWfService;

    @ApiOperation("批量更新配料任务头信息")
    @PutMapping
    public RestResponse batchUpdate(@RequestBody WipMcTaskUpdateDTO wipMcTaskUpdateDTO) {
        wipMcTaskService.batchUpdate(wipMcTaskUpdateDTO);
        return ResponseFactory.getOkResponse(null);
    }

    @ApiOperation(value = "更新配料任务行信息")
    @PutMapping("/line")
    public RestResponse batchUpdateTaskLine(@RequestBody WipMcTaskLineUpdateDTO wipMcTaskLineUpdateDTO) {
        wipMcTaskLineService.batchUpdateBySourceLine(wipMcTaskLineUpdateDTO);
        return ResponseFactory.getOkResponse(null);
    }

    @ApiOperation(value = "开立配料任务")
    @PostMapping
    public RestResponse saveWipMcTaskSaveDTO(@RequestBody WipMcTaskSaveDTO wipMcTaskSaveDTO) {
        ;
        return ResponseFactory.getOkResponse(wipMcTaskService.saveWipMcTaskSaveDTO(wipMcTaskSaveDTO));
    }

    @ApiOperation("获取配料任务行状态")
    @GetMapping("/line/status")
    public RestResponse listWipLineStatusView(@ModelAttribute WipMcLineStatusQuery query) {
        return ResponseFactory.getOkResponse(wipMcTaskService.listWipLineStatusView(query));
    }

    @ApiOperation(value = "配料任务状态变更", notes = "配料任务状态变更")
    @PatchMapping("/line/status")
    public RestResponse updateStatusBySourceLindId(@RequestBody WipMcTaskUpdateStatusDTO wipMcTaskUpdateStatusDTO) {
        wipMcTaskService.updateStatusBySourceLine(wipMcTaskUpdateStatusDTO);
        return ResponseFactory.getOkResponse(null);
    }


    @ApiOperation(value = "配料任务状态解除锁定")
    @PatchMapping("/line/status/unlock")
    public RestResponse unlockBySourceLineIds(@RequestBody WipMcTaskUnlockDTO wipMcTaskUnlockDTO) {
        wipMcWfService.unlockBySourceLineIds(wipMcTaskUnlockDTO);
        return ResponseFactory.getOkResponse(null);
    }

}
