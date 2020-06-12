package com.cvte.scm.wip.domain.common.attachment.entity;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * @author zy
 * @date 2020-05-25 10:17
 **/
@Data
@Accessors(chain = true)
public class WipAttachmentEntity extends BaseModel {

    private static final long serialVersionUID = 1L;


    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * 实体id
     */
    @ApiModelProperty(value = "实体id")
    private String referenceId;
    private String referenceType;
    /**
     * 文件名
     */
    @ApiModelProperty(value = "文件名")
    private String fileName;
    /**
     * 文件类型
     */
    @ApiModelProperty(value = "文件类型")
    private String contentType;
    /**
     * 附件id
     */
    @ApiModelProperty(value = "附件id")
    private String attPathId;
    /**
     * 附加大小
     */
    @ApiModelProperty(value = "附加大小")
    private Double fileSize;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk01;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk02;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk03;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk04;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk05;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String crtUser;
    @ApiModelProperty(value = "创建人")
    private String crtName;
    /**
     * 创建主机
     */
    @ApiModelProperty(value = "创建主机")
    private String crtHost;
    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;
    /**
     * 最后修改人
     */
    @ApiModelProperty(value = "最后修改人")
    private String updUser;
    /**
     * 最后修改主机
     */
    @ApiModelProperty(value = "最后修改主机")
    private String updHost;
    /**
     * 最后修改时间
     */
    @ApiModelProperty(value = "最后修改时间")
    private Date updTime;
    @ApiModelProperty(value = "最后修改时间")
    private String updName;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String isDel;
}
