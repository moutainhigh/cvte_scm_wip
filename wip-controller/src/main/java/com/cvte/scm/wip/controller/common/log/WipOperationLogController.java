package com.cvte.scm.wip.controller.common.log;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.common.log.dto.WipLogDTO;
import com.cvte.scm.wip.domain.common.log.service.WipOperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zy
 * @date 2020-05-22 17:23
 **/
@Api(tags = "配料任务接口")
@RestController
@RequestMapping("/admin/log")
@Slf4j
public class WipOperationLogController {

    @Autowired
    private WipOperationLogService wipOperationLogService;

    @PostMapping
    @ApiOperation(value = "保存操作日志")
    public RestResponse addLog(@RequestBody WipLogDTO wipLogDTO) {
        wipOperationLogService.addLog(wipLogDTO);
        return ResponseFactory.getOkResponse(null);
    }
}
