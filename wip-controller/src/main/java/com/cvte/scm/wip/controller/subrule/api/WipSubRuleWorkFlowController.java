package com.cvte.scm.wip.controller.subrule.api;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.core.subrule.service.WipSubRuleWfCallbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/2/26 14:46
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Api(tags = "临时代用单工作流接口")
@RestController
@RequestMapping("/api/req/sub_rule")
@Slf4j
public class WipSubRuleWorkFlowController {

    private WipSubRuleWfCallbackService wipSubRuleWfCallbackService;

    public WipSubRuleWorkFlowController(WipSubRuleWfCallbackService wipSubRuleWfCallbackService) {
        this.wipSubRuleWfCallbackService = wipSubRuleWfCallbackService;
    }

    @GetMapping("/auditors")
    @ApiOperation(value = "获取业务系统字段属性")
    public RestResponse getAuditors(@RequestParam(required = false) String formInstanceId,
                                    @RequestParam(required = false) List<String> fields) {
        Map<String, String> fieldsMap = wipSubRuleWfCallbackService.getAuditors(formInstanceId, fields);
        return new RestResponse().setData(fieldsMap);
    }
}
