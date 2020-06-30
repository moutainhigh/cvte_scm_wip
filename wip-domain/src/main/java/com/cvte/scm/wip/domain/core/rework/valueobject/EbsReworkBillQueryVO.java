package com.cvte.scm.wip.domain.core.rework.valueobject;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/17 10:35
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
@ApiModel(value = "获取EBS返工单的数据传输对象")
public class EbsReworkBillQueryVO {

    @ApiModelProperty(value = "返工单头ID")
    private String bill_header_id;

    @ApiModelProperty(value = "返工单号")
    private String bill_no;

    @ApiModelProperty(value = "返工单状态")
    private String bill_status;

    @ApiModelProperty(value = "库存组织ID")
    private String organization_id;

    @ApiModelProperty(value = "工厂ID")
    private String project_id;

    @ApiModelProperty(value = "返工类型")
    private String rework_type;

    @ApiModelProperty(value = "更改原因")
    private String rework_reason_type;

    @ApiModelProperty(value = "更改原因详述")
    private String rework_reason;

    @ApiModelProperty(value = "更改要求")
    private String rework_desc;

    @ApiModelProperty(value = "责任事业部")
    private String resp_bu;

    @ApiModelProperty(value = "责任部门")
    private String resp_dept;

    @ApiModelProperty(value = "期望完成时间")
    private Date date_required;

    @ApiModelProperty(value = "预计出货时间")
    private Date product_out_date;

    @ApiModelProperty(value = "不良品处理方式")
    private String rejects_deal_type;

    @ApiModelProperty(value = "良品处理方式")
    private String goods_deal_type;

    @ApiModelProperty(value = "不良品物料处理方式")
    private String reject_mtr_deal_type;

    @ApiModelProperty(value = "良品物料处理方式")
    private String good_mtr_deal_type;


    @ApiModelProperty(value = "法人组织ID")
    private String org_id;

    @ApiModelProperty(value = "EBS请求ID")
    private String request_id;

    @ApiModelProperty(value = "创建人")
    private String created_by;

    @ApiModelProperty(value = "创建时间")
    private Date creation_date;

    @ApiModelProperty(value = "更新人")
    private String last_updated_by;

    @ApiModelProperty(value = "更新时间")
    private Date last_update_date;

}
