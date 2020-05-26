package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.app.requirement.application.WipItemWkpPosApplication;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author zy
 * @date 2020-05-25 17:26
 **/
@Slf4j
@RestController
@Api(tags = "投料单头接口")
@RequestMapping("/admin/req/item/wkp")
public class WipItemWkpPosController {

    @Autowired
    private WipItemWkpPosApplication wipItemWkpPosApplication;


    @PostMapping("/import")
    public RestResponse importExcel(@RequestParam("file") MultipartFile multipartFile) {
        wipItemWkpPosApplication.importExcel(multipartFile);
        return ResponseFactory.getOkResponse(null);
    }


}
