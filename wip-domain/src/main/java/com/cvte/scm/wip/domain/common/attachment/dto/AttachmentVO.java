package com.cvte.scm.wip.domain.common.attachment.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author zy
 * @date 2020-04-29 19:34
 **/
@Getter
@Setter
@Accessors(chain = true)
public class AttachmentVO {


    private String id;

    @ApiModelProperty(value="附件路径ID")
    private String attPathId;

    @ApiModelProperty(value="实体ID")
    private String referenceId;


    @ApiModelProperty(value="文件名")
    private String fileName;

    @ApiModelProperty(value="文件大小")
    private Long fileSize;

    @ApiModelProperty(value="文件类型")
    private String contentType;

    @ApiModelProperty(value = "创建时间")
    private Date crtTime;

    private String crtName;
}
