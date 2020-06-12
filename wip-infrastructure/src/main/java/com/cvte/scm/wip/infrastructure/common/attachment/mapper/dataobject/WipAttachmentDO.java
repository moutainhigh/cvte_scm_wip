package com.cvte.scm.wip.infrastructure.common.attachment.mapper.dataobject;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 附件表
 *
 * @author zy
 * @since 2020-04-29
 */
@Table(name = "wip_attachment")
@ApiModel(description = "附件表")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipAttachmentDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * 实体id
     */
    @Column(name = "reference_id")
    @ApiModelProperty(value = "实体id")
    private String referenceId;

    @Column(name = "reference_type")
    @ApiModelProperty(value = "实体类型")
    private String referenceType;
    /**
     * 文件名
     */
    @Column(name = "file_name")
    @ApiModelProperty(value = "文件名")
    private String fileName;
    /**
     * 文件类型
     */
    @Column(name = "content_type")
    @ApiModelProperty(value = "文件类型")
    private String contentType;
    /**
     * 附件id
     */
    @Column(name = "att_path_id")
    @ApiModelProperty(value = "附件id")
    private String attPathId;
    /**
     * 附加大小
     */
    @Column(name = "file_size")
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
    @Column(name = "crt_user")
    @ApiModelProperty(value = "创建人")
    private String crtUser;
    @Column(name = "crt_name")
    @ApiModelProperty(value = "创建人")
    private String crtName;
    /**
     * 创建主机
     */
    @Column(name = "crt_host")
    @ApiModelProperty(value = "创建主机")
    private String crtHost;
    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    @ApiModelProperty(value = "创建时间")
    private Date crtTime;
    /**
     * 最后修改人
     */
    @Column(name = "upd_user")
    @ApiModelProperty(value = "最后修改人")
    private String updUser;
    /**
     * 最后修改主机
     */
    @Column(name = "upd_host")
    @ApiModelProperty(value = "最后修改主机")
    private String updHost;
    /**
     * 最后修改时间
     */
    @Column(name = "upd_time")
    @ApiModelProperty(value = "最后修改时间")
    private Date updTime;
    @Column(name = "upd_name")
    @ApiModelProperty(value = "最后修改时间")
    private String updName;
    /**
     * ${field.comment}
     */
    @Column(name = "is_del")
    @ApiModelProperty(value = "${field.comment}")
    private String isDel;

}
