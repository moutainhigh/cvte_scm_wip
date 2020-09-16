package com.cvte.scm.wip.infrastructure.patch.mapper.dataobject;


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
 * ${table.comment}
 *
 * @author author
 * @since 2020-07-25
 */
@Table(name = "wip_patch_wf")
@ApiModel(description = "${table.comment}")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class WipPatchWfDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "自增ID")
    private Integer id;
    /**
     * 补料单id
     */
    @Column(name = "bill_id")
    @ApiModelProperty(value = "补料单id")
    private String billId;
    /**
     * 10 新建、20 已提交、30 已确认、25 已驳回、40需重新提交、80已关闭
     */
    @ApiModelProperty(value = "10 新建、20 已提交、30 已确认、25 已驳回、40需重新提交、80已关闭")
    @Column(name = "status")
    private String status;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_date")
    @ApiModelProperty(value = "${field.comment}")
    private Date crtDate;

}
