package com.cvte.scm.wip.controller.change.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.changebill.parse.ChangeBillParseApplication;
import com.cvte.scm.wip.app.changebill.sync.ChangeBillFullSyncApplication;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/9 20:42
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@RestController
@Api(tags = "更改单指令相关接口")
@RequestMapping("/admin/change/ins")
public class ChangeOrderInsController {

    private ChangeBillParseApplication changeBillParseApplication;
    private ChangeBillFullSyncApplication changeBillFullSyncApplication;

    public ChangeOrderInsController(ChangeBillParseApplication changeBillParseApplication, ChangeBillFullSyncApplication changeBillFullSyncApplication) {
        this.changeBillParseApplication = changeBillParseApplication;
        this.changeBillFullSyncApplication = changeBillFullSyncApplication;
    }

    @PostMapping("/sync")
    public RestResponse sync(@RequestBody ChangeBillQueryVO vo) {
        changeBillParseApplication.doAction(vo);
        return new RestResponse();
    }

    @PostMapping("/full_sync")
    public RestResponse fullSync(@RequestBody ChangeBillQueryVO vo) {
        changeBillFullSyncApplication.doAction(vo);
        return new RestResponse();
    }

}
