package com.cvte.scm.wip.controller.subrule.admin;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.common.enums.ExecutionModeEnum;
import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleEntity;
import com.cvte.scm.wip.domain.core.subrule.service.WipSubRuleService;
import com.cvte.scm.wip.domain.core.subrule.service.WipSubRuleWfCallbackService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : jf
 * Date    : 2019.12.30
 * Time    : 15:02
 * Email   ：jiangfeng7128@cvte.com
 */
@Slf4j
@RestController
@Api(tags = "临时代用单")
@RequestMapping("/admin/req/sub_rule")
public class WipSubRuleController {

    private WipSubRuleService wipSubRuleService;
    private WipSubRuleWfCallbackService wipSubRuleWfCallbackService;

    public WipSubRuleController(WipSubRuleService wipSubRuleService, WipSubRuleWfCallbackService wipSubRuleWfCallbackService) {
        this.wipSubRuleService = wipSubRuleService;
        this.wipSubRuleWfCallbackService = wipSubRuleWfCallbackService;
    }

    @PostMapping("/addOne")
    public RestResponse addOne(@RequestBody WipSubRuleEntity wipSubRule) {
        wipSubRuleService.addOne(wipSubRule, ExecutionModeEnum.STRICT);
        return ResponseFactory.buildResult("创建成功！", wipSubRule.getRuleId());
    }

    @PostMapping("/update")
    public RestResponse update(@RequestBody List<WipSubRuleEntity> wipSubRuleList) {
        wipSubRuleService.update(wipSubRuleList, ExecutionModeEnum.STRICT);
        return ResponseFactory.buildResult("创建成功！", wipSubRuleList.stream().map(WipSubRuleEntity::getRuleId).collect(Collectors.toList()));
    }

    @PostMapping("/invalid")
    public RestResponse invalid(@RequestBody String... ruleIds) {
        wipSubRuleService.invalid(ExecutionModeEnum.STRICT, ruleIds);
        return ResponseFactory.getOkResponse("作废成功！");
    }

    @GetMapping("/getSubRuleData/{ruleId}")
    public RestResponse getSubRuleData(@PathVariable("ruleId") String ruleId) {
        return ResponseFactory.getOkResponse(wipSubRuleService.getSubRuleData(ruleId));
    }

    @PostMapping("/getSubRuleDetail")
    public RestResponse getSubRuleDetail(@RequestBody List<String> params) {
        if (params == null || params.size() < 3) {
            log.error("[scm-wip][sub-rule][rule-detail] ");
            throw new ParamsIncorrectException("抱歉，系统出现了未知错误，速联系相关人员。");
        }
        /* 索引 0 为规则编号，索引 1 为替换前物料编码，索引 2 为替换后物料编码 */
        return ResponseFactory.getOkResponse(wipSubRuleService.getSubRuleDetail(params.get(0)).setRmk01(params.get(1)).setRmk02(params.get(2)));
    }

    @PostMapping("/create_work_flow")
    public RestResponse createTest(@RequestBody WipSubRuleEntity subRule) {
        String response = wipSubRuleWfCallbackService.createSubRuleWorkFlow(subRule.getRuleId());
        return new RestResponse().setData(response);
    }
}