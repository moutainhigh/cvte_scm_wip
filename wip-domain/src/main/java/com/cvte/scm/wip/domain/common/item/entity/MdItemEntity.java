package com.cvte.scm.wip.domain.common.item.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Table;

/**
 * @version 1.0
 * @descriptions: 仅仅用作查询物料信息
 * @author: ykccchen
 * @date: 2020/7/24 2:07 下午
 */
@Data
@Table(name = "scm.md_item")
@Accessors(chain = true)
public class MdItemEntity {

    @ApiModelProperty("物料ID")
    private String inventoryItemId;

    @ApiModelProperty("物料编号")
    private String itemCode;

    @ApiModelProperty("机型")
    private String partFamily;

    @ApiModelProperty("物料描述")
    private String itemDesc;

    @ApiModelProperty("物料状态")
    private String inventoryItemStatusCode;
}
