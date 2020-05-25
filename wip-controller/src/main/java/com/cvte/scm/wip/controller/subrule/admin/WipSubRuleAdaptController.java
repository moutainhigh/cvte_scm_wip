package com.cvte.scm.wip.controller.subrule.admin;

import com.cvte.csb.core.exception.client.params.ParamsIncorrectException;
import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.core.subrule.service.WipSubRuleAdaptService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author : jf
 * Date    : 2019.12.30
 * Time    : 15:02
 * Email   ：jiangfeng7128@cvte.com
 */
@Slf4j
@RestController
@RequestMapping("/admin/req/sub_rule/adapt")
public class WipSubRuleAdaptController {

    private WipSubRuleAdaptService wipSubRuleAdaptService;

    public WipSubRuleAdaptController(WipSubRuleAdaptService wipSubRuleAdaptService) {
        this.wipSubRuleAdaptService = wipSubRuleAdaptService;
    }

    @PostMapping("/getLotDetailByRuleId")
    public RestResponse getLotDetail(@RequestBody String[] params) {
        if (params == null || params.length != 2) {
            throw new ParamsIncorrectException("抱歉，系统出现未知错误，速联系相关人员。");
        }
        /* 索引 0 处为 组织ID，索引 1 处为 规则ID。*/
        return ResponseFactory.getOkResponse(wipSubRuleAdaptService.getSubRuleLotDetailData(params[0], params[1]));
    }

    @PostMapping("/getLotDetailByScopeType")
    public RestResponse getLotDetail(@RequestBody List<String> params) {
        if (params == null || params.size() < 3) {
            throw new ParamsIncorrectException("抱歉，系统出现未知错误，速联系相关人员。");
        }
        String organizationId = params.get(0), scopeType = params.get(1);
        /* 索引 0 处为 组织ID，索引 1 处为适用类型，后续的值为匹配对象。*/
        return ResponseFactory.getOkResponse(wipSubRuleAdaptService.getSubRuleLotDetailData(organizationId, scopeType, params.subList(2, params.size())));
    }
}