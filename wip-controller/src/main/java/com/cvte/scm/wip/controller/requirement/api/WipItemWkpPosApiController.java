package com.cvte.scm.wip.controller.requirement.api;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.core.requirement.entity.WipItemWkpPosEntity;
import com.cvte.scm.wip.domain.core.requirement.service.WipItemWkpPosService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-28 11:15
 **/
@Api(tags = "投料单变更接口")
@RestController
@RequestMapping("/api/req/item/wkp")
@Slf4j
public class WipItemWkpPosApiController {

    @Autowired
    private WipItemWkpPosService wipItemWkpPosService;

    @ApiOperation(value = "批量保存")
    @PostMapping("/batch")
    public RestResponse batchSave(List<WipItemWkpPosEntity> wipItemWkpPosEntities) {
        wipItemWkpPosService.batchSave(wipItemWkpPosEntities);
        return ResponseFactory.getOkResponse(null);
    }
}
