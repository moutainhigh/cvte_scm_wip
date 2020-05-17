package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLineEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import java.util.Date;

/**
 * @author jf
 * @since 2019-12-30
 */
@Data
@Accessors(chain = true)
@ApiModel(description = "投料单")
@Table(name = "wip.wip_req_lines")
public class WipReqLineDO {

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "line_id")
    @ApiModelProperty(value = "${field.comment}")
    private String lineId;
    /**
     * 投料单头ID，来源于wip_req_header表。
     */
    @Column(name = "header_id")
    @ApiModelProperty(value = "投料单头ID，来源于wip_req_header表。")
    private String headerId;
    /**
     * ${field.comment}
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "${field.comment}")
    private String organizationId;
    /**
     * 工序号
     */
    @Column(name = "wkp_no")
    @ApiModelProperty(value = "工序号")
    private String wkpNo;
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
     * 物料编码
     */
    @Column(name = "item_no")
    @ApiModelProperty(value = "物料编码")
    private String itemNo;

    @Transient
    @ApiModelProperty(value = "物料编码")
    private String beforeItemNo;

    @Column(name = "item_id")
    @ApiModelProperty(value = "物料ID")
    private String itemId;
    /**
     * 单位用量
     */
    @Column(name = "unit_qty")
    @ApiModelProperty(value = "单位用量")
    @DecimalMin(value = "0.0", message = "单位用量必须大于等于零")
    @Digits(integer = 10, fraction = 6, message = "单位用量只能保留6位小数")
    private Double unitQty;
    /**
     * 需求数量
     */
    @Column(name = "req_qty")
    @ApiModelProperty(value = "需求数量")
    @DecimalMin(value = "0", message = "需求数量必须大于等于零")
    private Integer reqQty;
    /**
     * 已发料数量
     */
    @Column(name = "issued_qty")
    @ApiModelProperty(value = "已发料数量")
    @DecimalMin(value = "0", message = "已发料数量必须大于等于零")
    private Integer issuedQty;
    /**
     * 超发原因
     */
    @Column(name = "exceed_reason")
    @ApiModelProperty(value = "超发原因")
    private String exceedReason;
    /**
     * 原始行ID
     */
    @Column(name = "original_line_id")
    @ApiModelProperty(value = "原始行ID")
    private String originalLineId;
    /**
     * 变更前行ID
     */
    @Column(name = "before_line_id")
    @ApiModelProperty(value = "变更前行ID")
    private String beforeLineId;
    /**
     * 行状态
     */
    @Column(name = "line_status")
    @ApiModelProperty(value = "行状态")
    private String lineStatus;
    /**
     * 变更原因
     */
    @Column(name = "change_reason")
    @ApiModelProperty(value = "变更原因")
    private String changeReason;

    @Column(name = "line_version")
    @ApiModelProperty(value = "行版本，主要为了标记被取消数据，如果数据行被取消，则将行ID记录其中，否则为空")
    private String lineVersion;

    @Column(name = "source_code")
    @ApiModelProperty(value = "来源编号")
    private String sourceCode = "SCM-WIP";

    @Column(name = "enable_date")
    @ApiModelProperty(value = "生效时间")
    private Date enableDate;

    @Column(name = "disable_date")
    @ApiModelProperty(value = "失效时间")
    private Date disableDate;

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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updDate;

    @Transient
    private String groupId;

    public static WipReqLineEntity buildEntity(WipReqLineDO lineDO) {
        WipReqLineEntity lineEntity = new WipReqLineEntity();
        BeanUtils.copyProperties(lineDO, lineEntity);
        return lineEntity;
    }

    public static WipReqLineDO buildDO(WipReqLineEntity lineEntity) {
        WipReqLineDO lineDO = new WipReqLineDO();
        BeanUtils.copyProperties(lineEntity, lineDO);
        return lineDO;
    }

}