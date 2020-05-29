package com.cvte.scm.wip.domain.core.ckd.entity;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;


/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-04-28
 */
@ApiModel(description = "${table.comment}")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcReqToTaskEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String mcReqId;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String mcTaskId;

}
