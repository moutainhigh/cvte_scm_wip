package com.cvte.scm.wip.domain.thirdpart.thirdpart.dto;

import com.cvte.csb.sys.common.MyBaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Transient;
import java.util.List;

/**
 * @author zy
 * @date 2020-05-11 09:45
 **/
@Data
@Accessors(chain = true)
public class EbsInoutStockDTO extends MyBaseEntity {

    @ApiModelProperty(value = "唯一标识")
    private String interfaceOrigSourceId;


    @ApiModelProperty(value = "导入类型：INSERT或UPDATE")
    private String interfaceAction;

    @ApiModelProperty(value = "库存组织: INV_SY")
    private String organizationName;

    @ApiModelProperty(value = "事务处理来源类型: 固定“库存”")
    private String txnSourceTypeName;

    @ApiModelProperty(value = "事务处理类型, 厂内调拨")
    private String transactionTypeName;

    @ApiModelProperty(value = "事业部")
    private String buName;

    @ApiModelProperty(value = "部门")
    private String deptName;

    @ApiModelProperty(value = "申请者中文名")
    private String reqEmployeeNum;

    @ApiModelProperty(value = "备注，默认为CKD配料任务号")
    private String description;

    private List<LineDTO> importLnJson;


    @Data
    @Accessors(chain = true)
    public static class LineDTO extends MyBaseEntity {

        private String interfaceOrigSourceId;

        @ApiModelProperty(value = "INSERT或UPDATE")
        private String interfaceAction;

        @ApiModelProperty(value = "行号")
        private String lineNo;

        @ApiModelProperty(value = "物料编码")
        private String itemNo;

        @ApiModelProperty(value = "仓库")
        private String subinventory;

        @ApiModelProperty(value = "目的仓库")
        private String toSubinventory;


        @ApiModelProperty(value = "货位")
        private String locatorCode;

        @ApiModelProperty(value = "计划数量")
        private String planQty;

        @ApiModelProperty(value = "实际数量")
        private String actualQty;

        @Transient
        @ApiModelProperty(value = "配料任务行id")
        private String mcTaskLineId;

    }



}
