package com.cvte.scm.wip.domain.common.attachment.dto;

import com.cvte.csb.sys.common.MyBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import jodd.vtor.constraint.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zy
 * @date 2020-04-29 19:12
 **/
@Data
@Accessors(chain = true)
public class AttachmentDTO extends MyBaseEntity {

    private String id;

    @ApiModelProperty(value="附件路径ID")
    @NotNull(message = "附件路径id不能为空")
    private String attPathId;

    @ApiModelProperty(value="实体ID")
    @NotNull(message = "附件关联实体id不能为空")
    private String referenceId;

    @ApiModelProperty(value="文件名")
    @NotNull(message = "附件名不能为空")
    private String fileName;

    @NotNull(message = "附件大小不能为空")
    @ApiModelProperty(value="文件大小")
    private Long fileSize;

    @ApiModelProperty(value="文件类型")
    private String contentType;

}
