package com.cvte.scm.wip.domain.core.requirement.entity;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import java.util.Date;

/**
 * @author yt
 * @since 2020-01-17
 */
@Data
@Accessors(chain = true)
public class WipReqLotIssuedEntity {

    private String id;


    @ApiModelProperty(value = "投料单头ID")
    @NotBlank(message = "投料单头不可为空")
    private String headerId;

    @ApiModelProperty(value = "库存组织")
    @NotBlank(message = "库存组织不可为空")
    private String organizationId;

    @ApiModelProperty(value = "物料编码")
    @NotBlank(message = "物料编码不可为空")
    private String itemNo;

    @ApiModelProperty(value = "工序")
    @NotBlank(message = "工序不可为空")
    private String wkpNo;

    @ApiModelProperty(value = "领料批次")
    @NotBlank(message = "领料批次不可为空")
    private String mtrLotNo;

    @ApiModelProperty(value = "领料数量")
    @Min(value = 0, message = "最小领料数量为0")
    private Integer issuedQty;

    @ApiModelProperty(value = "状态")
    private String status;

    private String rmk01;

    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;

    private String crtUser;

    private Date crtDate;

    private String updUser;

    private Date updDate;
}