package com.cvte.scm.wip.controller.requirement.api;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.domain.core.requirement.service.WipReqInterfaceService;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedModeEnum;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author : jf
 * Date    : 2019.01.08
 * Time    : 14:55
 * Email   ：jiangfeng7128@cvte.com
 */
@Api(tags = "投料单变更接口")
@RestController
@RequestMapping("/api/req/interface")
@Slf4j
public class WipReqInterfaceController {

    private WipReqInterfaceService wipReqInterfaceService;

    public WipReqInterfaceController(WipReqInterfaceService wipReqInterfaceService) {
        this.wipReqInterfaceService = wipReqInterfaceService;
    }

    @PostMapping("/changeByInterfaceIds")
    public RestResponse changeByInterfaceIds(@RequestBody String... interfaceInIds) {
        wipReqInterfaceService.changeByInterfaceIdIds(ExecutionModeEnum.SLOPPY, ChangedModeEnum.AUTOMATIC, interfaceInIds);
        return ResponseFactory.getOkResponse("更改成功！");
    }

    @PostMapping("/changeByGroupIds")
    public RestResponse changeByGroupIds(@RequestBody String... groupIds) {
        wipReqInterfaceService.changeByGroupIds(ExecutionModeEnum.SLOPPY, ChangedModeEnum.AUTOMATIC, groupIds);
        return ResponseFactory.getOkResponse("更改成功！");
    }
}