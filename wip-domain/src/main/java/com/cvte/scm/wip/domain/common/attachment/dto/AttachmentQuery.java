package com.cvte.scm.wip.domain.common.attachment.dto;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zy
 * @date 2020-04-29 20:20
 **/
@Data
@Accessors(chain = true)
public class AttachmentQuery {

    private String id;

    private String referenceId;
}
