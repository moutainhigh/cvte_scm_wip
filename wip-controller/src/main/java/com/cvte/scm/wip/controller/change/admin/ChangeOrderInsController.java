package com.cvte.scm.wip.controller.change.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.scm.wip.app.changebill.parse.ChangeBillParseApplication;
import com.cvte.scm.wip.common.utils.DateUtils;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

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

    public ChangeOrderInsController(ChangeBillParseApplication changeBillParseApplication) {
        this.changeBillParseApplication = changeBillParseApplication;
    }

    @PostMapping("/sync")
    public RestResponse sync(@RequestBody ChangeBillQueryVO vo) {
        if (Objects.isNull(vo.getLastUpdDate())) {
            vo.setLastUpdDate(DateUtils.getMinutesBeforeTime(LocalDateTime.now(), 10));
        }
        changeBillParseApplication.doAction(vo);
        return new RestResponse();
    }
}
