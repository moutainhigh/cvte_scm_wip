package com.cvte.scm.wip.controller.requirement.admin;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.app.requirement.application.WipItemWkpPosApplication;
import com.cvte.scm.wip.domain.core.requirement.dto.WipItemWkpPostImportDTO;
import com.cvte.scm.wip.domain.core.requirement.service.WipItemWkpPosService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-25 17:26
 **/
@Slf4j
@RestController
@Api(tags = "物料工艺属性基表接口")
@RequestMapping("/admin/req/item/wkp")
public class WipItemWkpPosController {

    @Autowired
    private WipItemWkpPosApplication wipItemWkpPosApplication;

    @Autowired
    private WipItemWkpPosService wipItemWkpPosService;


    @PostMapping("/import")
    public RestResponse importExcel(@RequestParam("file") MultipartFile multipartFile) {
        wipItemWkpPosApplication.importExcel(multipartFile);
        return ResponseFactory.getOkResponse(null);
    }


    @PostMapping("/batch")
    public RestResponse batchSave(@RequestBody List<WipItemWkpPostImportDTO> wipItemWkpPostImportDTOS) {
        wipItemWkpPosApplication.saveImport(wipItemWkpPostImportDTOS);
        return ResponseFactory.getOkResponse(null);
    }

    @PostMapping("/batchDel")
    public RestResponse deleteByIds(@RequestBody List<String> ids) {
        wipItemWkpPosService.deleteByIds(ids);
        return ResponseFactory.getOkResponse(null);
    }


}
