package com.cvte.scm.wip.controller.ckd.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentDTO;
import com.cvte.scm.wip.domain.core.ckd.dto.WipMcTaskUpdateStatusDTO;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcTaskVersionEntity;
import com.cvte.scm.wip.domain.core.ckd.enums.TransactionTypeNameEnum;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskService;
import com.cvte.scm.wip.domain.core.ckd.service.WipMcTaskVersionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zy
 * @date 2020-04-29 15:56
 **/
@Api(tags = "配料任务接口")
@RestController
@RequestMapping("/admin/mc/task")
@Slf4j
public class WipMcTaskController {

    @Autowired
    private WipMcTaskVersionService wipMcTaskVersionService;

    @Autowired
    private WipMcTaskService wipMcTaskService;


    @ApiOperation(value = "获取配料任务版本比较信息", notes = "获取配料任务版本比较信息")
    @GetMapping("/{mcTaskId}/version/compare")
    public RestResponse compareVersion(@PathVariable String mcTaskId,
                                       @RequestParam("compareVersionId") String compareVersionId,
                                       @RequestParam("compareVersionToId") String compareToVersionId) {
        return ResponseFactory.getOkResponse(wipMcTaskVersionService.compareTaskVersion(compareVersionId, compareToVersionId));
    }

    @ApiOperation(value = "配料任务版本列表", notes = "配料任务版本列表")
    @GetMapping("/{mcTaskId}/version")
    public RestResponse listWipMcTaskVersion(@PathVariable String mcTaskId) {
        List<WipMcTaskVersionEntity> wipMcTaskVersionEntities = wipMcTaskVersionService.listWipMcTaskVersion(mcTaskId);
        return ResponseFactory.getOkResponse(wipMcTaskVersionEntities);
    }

    @ApiOperation(value = "获取配料任务详情", notes = "获取配料任务详情")
    @GetMapping("/{mcTaskId}")
    public RestResponse getMcTaskInfoView(@PathVariable String mcTaskId) {
        return ResponseFactory.getOkResponse(wipMcTaskService.getMcTaskInfoView(mcTaskId));
    }


    @ApiOperation(value = "配料任务状态变更", notes = "配料任务状态变更")
    @PatchMapping("/{mcTaskId}/status")
    public RestResponse updateStatus(@PathVariable String mcTaskId, @RequestBody WipMcTaskUpdateStatusDTO wipMcTaskUpdateStatusDTO) {
        wipMcTaskService.updateStatus(mcTaskId, wipMcTaskUpdateStatusDTO.getUpdateToStatus());
        return ResponseFactory.getOkResponse(null);
    }


    @PostMapping("/{mcTaskId}/stock/{versionId}/in")
    public RestResponse inStock(@PathVariable String mcTaskId,
                                @PathVariable String versionId) {
        wipMcTaskService.inoutStock(TransactionTypeNameEnum.IN, mcTaskId, versionId);
        return ResponseFactory.getOkResponse(null);
    }

    @PostMapping("/{mcTaskId}/stock/{versionId}/out")
    public RestResponse outStock(@PathVariable String mcTaskId,
                                 @PathVariable String versionId) {
        wipMcTaskService.inoutStock(TransactionTypeNameEnum.OUT, mcTaskId, versionId);
        return ResponseFactory.getOkResponse(null);
    }


    @PostMapping("/attachment")
    public RestResponse save(@RequestBody List<AttachmentDTO> attachmentSaveDTOList) {
        wipMcTaskService.saveBatchAttachment(attachmentSaveDTOList);
        return ResponseFactory.getOkResponse(null);
    }

    @DeleteMapping("/attachment/{id}")
    public RestResponse removeByFileId(@PathVariable String id) {
        wipMcTaskService.removeAttachmentById(id);
        return ResponseFactory.getOkResponse(null);
    }

}
