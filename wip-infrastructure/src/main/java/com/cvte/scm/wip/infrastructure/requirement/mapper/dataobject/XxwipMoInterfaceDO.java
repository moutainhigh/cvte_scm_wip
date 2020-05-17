package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.Objects;

/**
 * WIP工单接口表
 *
 * @author jf
 * @since 2019-02-05
 */
@Table(name = "XXAPS.XXWIP_MO_INTERFACE")
@ApiModel(description = "WIP工单接口表")
@Data
@Accessors(chain = true)
public class XxwipMoInterfaceDO {
    @Id
    @Column(name = "INTERFACE_ID")
    @ApiModelProperty(value = "接口ID")
    private String interfaceId;

    @Column(name = "ORGANIZATION_ID")
    @ApiModelProperty(value = "组织ID")
    private String organizationId;

    @Column(name = "GROUP_ID")
    @ApiModelProperty(value = "群ID")
    private String groupId;

    @Column(name = "WIP_ENTITY_ID")
    @ApiModelProperty(value = "工单实体ID")
    private String wipEntityId;

    @Column(name = "LOT_NUMBER")
    @ApiModelProperty(value = "大批次号")
    private String lotNumber;

    @Column(name = "ITEM_ID")
    @ApiModelProperty(value = "物料ID")
    private String itemId;

    @Column(name = "ITEM_QTY")
    @ApiModelProperty(value = "物料数量")
    private Integer itemQty;

    @Column(name = "WKP_NO")
    @ApiModelProperty(value = "工序号")
    private String wkpNo;

    @Column(name = "POS_NO")
    @ApiModelProperty(value = "位号")
    private String posNo;

    @Column(name = "OPERATION_TYPE")
    @ApiModelProperty(value = "操作类型：add、delete、update和replace")
    private String operationType;

    @Column(name = "EXECUTE_CODE")
    @ApiModelProperty(value = "执行编号")
    private String executeCode;

    @Column(name = "CRT_USER")
    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;

    @Column(name = "CRT_TIME")
    @ApiModelProperty(value = "${field.comment}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date crtTime;

    @Column(name = "UPD_USER")
    @ApiModelProperty(value = "${field.comment}")
    private String updUser;

    @Column(name = "UPD_TIME")
    @ApiModelProperty(value = "${field.comment}")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updTime;

}