package com.cvte.scm.wip.controller.common.bu;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.domain.common.bu.service.SysBuService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/3/28 13:15
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@RestController
@RequestMapping("/admin/sys/bu")
public class SysBuController {

    private SysBuService buService;

    public SysBuController(SysBuService buService) {
        this.buService = buService;
    }

    @ApiOperation(value = "获取所有事业部信息")
    @GetMapping({"/listAll"})
    public RestResponse listAll() {
        return new RestResponse().setData(buService.getAll());
    }
}
