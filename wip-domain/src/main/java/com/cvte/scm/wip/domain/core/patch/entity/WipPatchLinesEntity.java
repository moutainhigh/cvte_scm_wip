package com.cvte.scm.wip.domain.core.patch.entity;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @version 1.0
 * @descriptions: 补料单行表
 * @author: ykccchen
 * @date: 2020/7/24 11:01 上午
 */
@Data
@Table(name = "wip_patch_lines")
@Accessors(chain = true)
public class WipPatchLinesEntity extends BaseModel {

    @ApiModelProperty("自增ID主键")
    @Id
    private Integer id;

    @ApiModelProperty("物料ID")
    private String itemId;

    @ApiModelProperty("物料编号")
    private String itemCode;

    @ApiModelProperty("自增ID主键")
    private String billId;

    @ApiModelProperty("数量")
    private Integer reqQty;

    @ApiModelProperty("最终数量")
    private Integer finalQty;

    @ApiModelProperty("审核/驳回原因")
    private String reason;

    @ApiModelProperty("删除情况")
    private Integer isShow;
}
