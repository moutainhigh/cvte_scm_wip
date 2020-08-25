package com.cvte.scm.wip.controller.requirement.admin;

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
@Slf4j
@RestController
@Api(tags = "投料单变更接口控制层")
@RequestMapping("/admin/req/interface")
public class WipReqInterfaceManualController {

    private WipReqInterfaceService wipReqInterfaceService;

    public WipReqInterfaceManualController(WipReqInterfaceService wipReqInterfaceService) {
        this.wipReqInterfaceService = wipReqInterfaceService;
    }

    @PostMapping("/count")
    public RestResponse count(@RequestBody String... headerId) {
        return ResponseFactory.getOkResponse("作废成功！").setData(wipReqInterfaceService.count(headerId[0]));
    }

    @PostMapping("/manual/changeByInterfaceIds")
    public RestResponse manualChangedByInterfaceIds(@RequestBody String... interfaceInIds) {
        wipReqInterfaceService.changeByInterfaceIdIds(ExecutionModeEnum.SLOPPY, ChangedModeEnum.MANUAL, interfaceInIds);
        return ResponseFactory.getOkResponse("更改成功！");
    }

    @PostMapping("/manual/invalid")
    public RestResponse invalid(@RequestBody String... interfaceInIds) {
        wipReqInterfaceService.invalid(interfaceInIds);
        return ResponseFactory.getOkResponse("作废成功！");
    }

}