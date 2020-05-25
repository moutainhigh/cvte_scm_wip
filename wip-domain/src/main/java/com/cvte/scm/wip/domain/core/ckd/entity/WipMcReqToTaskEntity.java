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
import javax.persistence.Table;


/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-04-28
 */
@Table(name = "wip_mc_req_to_task")
@ApiModel(description = "${table.comment}")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcReqToTaskEntity extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * ${field.comment}
     */
    @Column(name = "mc_req_id")
    @ApiModelProperty(value = "${field.comment}")
    private String mcReqId;
    /**
     * ${field.comment}
     */
    @Column(name = "mc_task_id")
    @ApiModelProperty(value = "${field.comment}")
    private String mcTaskId;

}
