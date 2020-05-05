package com.cvte.scm.demo.common.api;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.demo.common.service.HealthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: wufeng
 * @Date: 2019/12/25 16:19
 */
@Api(tags = "心跳检测")
@RestController
@RequestMapping("/api/health")
@Slf4j
public class HealthApiController {

    @Autowired
    private HealthService healthService;

    @ApiOperation(value = "心跳接口", notes = "心跳接口")
    @GetMapping("/detect")
    public RestResponse getMessage() {
        return ResponseFactory.getOkResponse(healthService.getMessage());
    }

}
