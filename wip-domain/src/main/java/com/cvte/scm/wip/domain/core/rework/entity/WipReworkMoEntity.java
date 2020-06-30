package com.cvte.scm.wip.domain.core.rework.entity;


import com.cvte.csb.validator.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;


/**
 * ${table.comment}
 *
 * @author author
 * @since 2020-03-28
 */
@Data
@Accessors(chain = true)
public class WipReworkMoEntity  {

    @ApiModelProperty(value = "工单ID")
    private Long sourceId;

    @ApiModelProperty(value = "库存组织ID")
    private Integer organizationId;

    private Long productId;

    @ApiModelProperty(value = "产品编号")
    private String productNo;

    @ApiModelProperty(value = "产品型号")
    private String productModel;

    @ApiModelProperty(value = "工厂ID")
    private Integer factoryId;

    @ApiModelProperty(value = "客户简称")
    private String consumerName;

    @ApiModelProperty(value = "工单批次号")
    private String sourceLotNo;

    private String orderNumber;

    @ApiModelProperty(value = "内勤")
    private String omName;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "客户产品编号")
    private String custItemNum;

    @ApiModelProperty(value = "客户产品型号")
    private String custModel;

    @ApiModelProperty(value = "客户批次号")
    private String custLotNumber;

    @ApiModelProperty(value = "批次状态")
    private String lotStatus;

    @ApiModelProperty(value = "在库天数")
    private Long availableDays;

    @ApiModelProperty(value = "可用库存数量")
    private Long invStockQty;

    @ApiModelProperty(value = "即时库存")
    private Long onhandQty;

    @ApiModelProperty(value = "累计库存更改单未领料数量")
    private Long cnQtyRequired;

    @ApiModelProperty(value = "理论库存")
    private Long reStockQty;

    
}
