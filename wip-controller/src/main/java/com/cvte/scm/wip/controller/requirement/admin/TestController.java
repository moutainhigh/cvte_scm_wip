package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.changebill.parse.ChangeBillParseApplication;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/22 20:11
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Slf4j
@RestController
@Api(tags = "投料单头接口")
@RequestMapping("/admin/change_order")
public class TestController {

    private ChangeBillParseApplication changeBillParseApplication;

    public TestController(ChangeBillParseApplication changeBillParseApplication) {
        this.changeBillParseApplication = changeBillParseApplication;
    }

    @PostMapping("test")
    public RestResponse test(@RequestBody ChangeBillQueryVO queryVO) {
        changeBillParseApplication.doAction(queryVO);
        return new RestResponse();
    }

}
