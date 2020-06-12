package com.cvte.scm.wip.controller.common.attachment;

import com.cvte.csb.core.interfaces.vo.RestResponse;
import com.cvte.csb.web.rest.ResponseFactory;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentDTO;
import com.cvte.scm.wip.domain.common.attachment.dto.AttachmentQuery;
import com.cvte.scm.wip.domain.common.attachment.service.WipAttachmentService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zy
 * @date 2020-04-29 19:52
 **/
@Api(tags = "配料任务接口")
@RestController
@RequestMapping("/admin/attachment")
@Slf4j
public class WipAttachmentController {

    @Autowired
    private WipAttachmentService wipAttachmentService;


    @PostMapping
    public RestResponse save(@RequestBody List<AttachmentDTO> attachmentSaveDTOList) {
        wipAttachmentService.saveBatch(attachmentSaveDTOList);
        return ResponseFactory.getOkResponse(null);
    }

    @GetMapping
    public RestResponse listAttachmentView(@ModelAttribute AttachmentQuery attachmentQuery) {
        return ResponseFactory.getOkResponse(wipAttachmentService.listAttachmentView(attachmentQuery));
    }

    @DeleteMapping("/{id}")
    public RestResponse removeByFileId(@PathVariable String id) {
        wipAttachmentService.removeById(id);
        return ResponseFactory.getOkResponse(null);
    }

}

