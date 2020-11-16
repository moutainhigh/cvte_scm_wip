package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.cvte.scm.wip.domain.core.requirement.entity.WipReqInterfaceEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author jf
 * @since 2019-12-30
 */
@Data
@Accessors(chain = true)
@Table(name = "wip.wip_req_interface")
@ApiModel(description = "投料单变更接口表")
public class WipReqInterfaceDO {

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "interface_in_id")
    @ApiModelProperty(value = "${field.comment}")
    private String interfaceInId;
    /**
     * ${field.comment}
     */
    @Column(name = "group_id")
    @ApiModelProperty(value = "${field.comment}")
    private String groupId;
    /**
     * ${field.comment}
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "${field.comment}")
    private String organizationId;
    /**
     * 投料单头ID，来源于wip_req_header表。
     */
    @Column(name = "header_id")
    @ApiModelProperty(value = "投料单头ID，来源于wip_req_header表。")
    private String headerId;
    /**
     * 投料单行ID，来源于wip_req_lines表。
     */
    @Column(name = "line_id")
    @ApiModelProperty(value = "投料单行ID，来源于wip_req_lines表。")
    private String lineId;
    /**
     * 工单号
     */
    @Column(name = "mo_number")
    @ApiModelProperty(value = "工单号")
    private String moNumber;
    /**
     * 批次号
     */
    @Column(name = "lot_number")
    @ApiModelProperty(value = "批次号")
    private String lotNumber;
    /**
     * 位号
     */
    @Column(name = "pos_no")
    @ApiModelProperty(value = "位号")
    private String posNo;
    /**
     * 物料编号
     */
    @Column(name = "item_no")
    @ApiModelProperty(value = "物料编号")
    private String itemNo;
    /**
     * 工序号
     */
    @Column(name = "wkp_no")
    @ApiModelProperty(value = "工序号")
    private String wkpNo;
    /**
     * 新物料编号
     */
    @Column(name = "item_no_new")
    @ApiModelProperty(value = "新物料编号")
    private String itemNoNew;
    /**
     * ${field.comment}
     */
    @Column(name = "item_qty")
    @ApiModelProperty(value = "${field.comment}")
    private Long itemQty;
    /**
     * 执行情况
     */
    @Column(name = "proceed")
    @ApiModelProperty(value = "执行情况")
    private String proceed;

    @Column(name = "exception_reason")
    @ApiModelProperty(value = "执行异常原因")
    private String exceptionReason;
    /**
     * 变更类型，ADD、DELETE和MODIFY
     */
    @Column(name = "change_type")
    @ApiModelProperty(value = "变更类型，add、delete、update和replace")
    private String changeType;
    /**
     * 来源单号
     */
    @Column(name = "source_bill_no")
    @ApiModelProperty(value = "来源单号")
    private String sourceBillNo;
    /**
     * 变更来源
     */
    @Column(name = "source_code")
    @ApiModelProperty(value = "变更来源")
    private String sourceCode;

    @Column(name = "priority")
    @ApiModelProperty(value = "优先级")
    private Integer priority;

    @Column(name = "need_confirm")
    @ApiModelProperty(value = "投料单行已备料，是否需要手工确认执行变更操作")
    private String needConfirm;

    @Column(name = "unit_qty")
    @ApiModelProperty(value = "单位数量")
    private Double unitQty;

    @Column(name = "issued_qty")
    @ApiModelProperty(value = "备料数量")
    private Long issuedQty;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk01;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk02;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk03;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk04;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk05;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk06;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk07;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk08;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk09;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk10;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk11;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk12;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk13;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk14;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String rmk15;
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
    /**
     * ${field.comment}
     */
    @Column(name = "upd_user")
    @ApiModelProperty(value = "${field.comment}")
    private String updUser;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_date")
    @ApiModelProperty(value = "${field.comment}")
    private Date updDate;

    public static WipReqInterfaceEntity buildEntity(WipReqInterfaceDO interfaceDO) {
        WipReqInterfaceEntity interfaceEntity = new WipReqInterfaceEntity();
        BeanUtils.copyProperties(interfaceDO, interfaceEntity);
        return interfaceEntity;
    }

    public static WipReqInterfaceDO buildDO(WipReqInterfaceEntity interfaceEntity) {
        WipReqInterfaceDO interfaceDO = new WipReqInterfaceDO();
        BeanUtils.copyProperties(interfaceEntity, interfaceDO);
        return interfaceDO;
    }

    public static List<WipReqInterfaceEntity> batchBuildEntity(List<WipReqInterfaceDO> interfaceDOList) {
        List<WipReqInterfaceEntity> interfaceEntityList = new ArrayList<>();
        for (WipReqInterfaceDO interfaceDO : interfaceDOList) {
            WipReqInterfaceEntity interfaceEntity = buildEntity(interfaceDO);
            interfaceEntityList.add(interfaceEntity);
        }
        return interfaceEntityList;
    }

    public static List<WipReqInterfaceDO> batchBuildDO(List<WipReqInterfaceEntity> interfaceEntityList) {
        List<WipReqInterfaceDO> interfaceDOList = new ArrayList<>();
        for (WipReqInterfaceEntity interfaceEntity : interfaceEntityList) {
            WipReqInterfaceDO interfaceDO = buildDO(interfaceEntity);
            interfaceDOList.add(interfaceDO);
        }
        return interfaceDOList;
    }

}
