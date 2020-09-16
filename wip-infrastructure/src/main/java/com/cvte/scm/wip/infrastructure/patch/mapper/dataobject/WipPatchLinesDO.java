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


/**
 * ${table.comment}
 *
 * @author author
 * @since 2020-07-25
 */
@Table(name = "wip_patch_lines")
@ApiModel(description = "${table.comment}")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class WipPatchLinesDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "自增ID")
    private Integer id;
    /**
     * 物料ID
     */
    @Column(name = "item_id")
    @ApiModelProperty(value = "物料ID")
    private String itemId;
    /**
     * 物料编号
     */
    @Column(name = "item_code")
    @ApiModelProperty("物料编号")
    private String itemCode;
    /**
     * 补料单ID
     */
    @Column(name = "bill_id")
    @ApiModelProperty(value = "补料单ID")
    private String billId;
    /**
     * 数量
     */
    @Column(name = "req_qty")
    @ApiModelProperty(value = "数量")
    private Integer reqQty;
    /**
     * 最终数量
     */
    @ApiModelProperty("最终数量")
    private Integer finalQty;
    /**
     * ${field.comment}
     */
    @Column(name = "reason")
    @ApiModelProperty(value = "${field.comment}")
    private String reason;

    @Column(name = "is_show")
    @ApiModelProperty("删除情况")
    private String isShow;

}
