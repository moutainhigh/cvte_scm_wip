package com.cvte.scm.wip.controller.subrule.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.core.subrule.service.WipSubRuleItemService;
import com.cvte.scm.wip.domain.core.subrule.valueobject.SubItemValidateVO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author : jf
 * Date    : 2019.03.05
 * Time    : 12:26
 * Email   ：jiangfeng7128@cvte.com
 */
@RestController
@RequestMapping("/admin/req/sub_rule/item")
public class WipSubRuleItemController {

    private WipSubRuleItemService wipSubRuleItemService;

    public WipSubRuleItemController(WipSubRuleItemService wipSubRuleItemService) {
        this.wipSubRuleItemService = wipSubRuleItemService;
    }

    @PostMapping("/getSubItemDetail/{organizationId}")
    public RestResponse getSubItemDetail(@PathVariable("organizationId") String organizationId,
                                         @RequestBody String[] subItemNos) {
        return ResponseFactory.getOkResponse(wipSubRuleItemService.getSubItemDetail(organizationId, subItemNos));
    }

    @PostMapping("/validate")
    public RestResponse validateSubItem(@RequestBody List<SubItemValidateVO> itemValidateDTOList) {
        return ResponseFactory.getOkResponse(wipSubRuleItemService.validateSubItem(itemValidateDTOList));
    }

}