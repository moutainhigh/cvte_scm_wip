package com.cvte.scm.wip.domain.core.ckd.entity;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.Date;

/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-04-29
 */
@ApiModel(description = "${table.comment}")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcWfEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @ApiModelProperty(value = "主键")
    private String wfId;
    /**
     * 配料任务ID
     */
    @ApiModelProperty(value = "配料任务ID")
    private String mcTaskId;
    /**
     * 状态（create:创建，verify：审批，reject：驳回，cancal:取消，confirm：工厂已确认，finish：完成，close：关闭）
     */
    @ApiModelProperty(value = "状态（create:创建，verify：审批，reject：驳回，cancal:取消，confirm：工厂已确认，finish：完成，close：关闭）")
    private String status;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private Date crtTime;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;

}
