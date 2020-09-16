package com.cvte.scm.wip.controller.patch.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.core.patch.entity.WipPatchEntity;
import com.cvte.scm.wip.domain.core.patch.service.WipPatchService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @descriptions:
 * @author: ykccchen
 * @date: 2020/7/24 3:54 下午
 */
@RestController
@RequestMapping("/admin/patch")
@Api(tags = "补料单主体信息")
@Slf4j
public class WipPatchController {

    private WipPatchService wipPatchService;

    public WipPatchController(WipPatchService wipPatchService) {
        this.wipPatchService = wipPatchService;
    }


    @PostMapping("/save/")
    public RestResponse insertBill(@RequestBody WipPatchEntity wipPatchEntity){
        String insert = wipPatchService.insert(wipPatchEntity);
        Map<String,String> map = new HashMap<>(4);
        map.put("billId",insert);
        return new RestResponse().setData(map);
    }
    @PostMapping("/save/{status}")
    public RestResponse updateBill(@RequestBody WipPatchEntity wipPatchEntity,
                                   @PathVariable("status") String status){
        wipPatchService.update(wipPatchEntity,status);
        return new RestResponse();
    }
}
