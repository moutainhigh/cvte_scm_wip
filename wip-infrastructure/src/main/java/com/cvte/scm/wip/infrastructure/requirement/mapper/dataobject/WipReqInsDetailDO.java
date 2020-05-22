package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;

import com.cvte.scm.wip.domain.core.requirement.entity.ReqInstructionDetailEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.persistence.*;
import com.cvte.csb.validator.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 投料单指令集
 *
 * @author author
 * @since 2020-05-22
 */
@Table(name = "wip_req_ins_d")
@ApiModel(description = "投料单指令集")
@Data
@EqualsAndHashCode
public class WipReqInsDetailDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 指令ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "ins_d_id")
    @ApiModelProperty(value = "指令ID")
    private String insDId;
    /**
     * 指令集ID
     */
    @Column(name = "ins_h_id")
    @ApiModelProperty(value = "指令集ID")
    private String insHId;
    /**
     * 组织ID
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "组织ID")
    private String organizationId;
    /**
     * 更改单明细ID
     */
    @Column(name = "source_cn_detail_id")
    @ApiModelProperty(value = "更改单明细ID")
    private String sourceCnDetailId;
    /**
     * 生产批次
     */
    @Column(name = "mo_lot_no")
    @ApiModelProperty(value = "生产批次")
    private String moLotNo;
    /**
     * 工序
     */
    @Column(name = "wkp_no")
    @ApiModelProperty(value = "工序")
    private String wkpNo;
    /**
     * 原物料ID
     */
    @Column(name = "item_id_old")
    @ApiModelProperty(value = "原物料ID")
    private String itemIdOld;
    /**
     * 新物料ID
     */
    @Column(name = "item_id_new")
    @ApiModelProperty(value = "新物料ID")
    private String itemIdNew;
    /**
     * 单位用量
     */
    @Column(name = "item_unit_qty")
    @ApiModelProperty(value = "单位用量")
    private Double itemUnitQty;
    /**
     * 用量
     */
    @Column(name = "item_qty")
    @ApiModelProperty(value = "用量")
    private Double itemQty;
    /**
     * 位号
     */
    @Column(name = "pos_no")
    @ApiModelProperty(value = "位号")
    private String posNo;
    /**
     * 操作类型
     */
    @Column(name = "operation_type")
    @ApiModelProperty(value = "操作类型")
    private String operationType;
    /**
     * 指令状态
     */
    @Column(name = "ins_status")
    @ApiModelProperty(value = "指令状态")
    private String insStatus;
    /**
     * 指令生效时间
     */
    @Column(name = "enable_date")
    @ApiModelProperty(value = "指令生效时间")
    private Date enableDate;
    /**
     * 指令失效时间
     */
    @Column(name = "disable_date")
    @ApiModelProperty(value = "指令失效时间")
    private Date disableDate;
    /**
     * 指令确认时间
     */
    @Column(name = "confirm_date")
    @ApiModelProperty(value = "指令确认时间")
    private Date confirmDate;
    /**
     * 指令确认人
     */
    @Column(name = "confirmed_by")
    @ApiModelProperty(value = "指令确认人")
    private String confirmedBy;
    /**
     * 指令失效人
     */
    @Column(name = "invalid_by")
    @ApiModelProperty(value = "指令失效人")
    private String invalidBy;
    /**
     * 指令失效原因
     */
    @Column(name = "invalid_reason")
    @ApiModelProperty(value = "指令失效原因")
    private String invalidReason;
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
    @Column(name = "crt_time")
    @ApiModelProperty(value = "${field.comment}")
    private Date crtTime;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_host")
    @ApiModelProperty(value = "${field.comment}")
    private String crtHost;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_time")
    @ApiModelProperty(value = "${field.comment}")
    private Date updTime;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_user")
    @ApiModelProperty(value = "${field.comment}")
    private String updUser;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_host")
    @ApiModelProperty(value = "${field.comment}")
    private String updHost;

    public static ReqInstructionDetailEntity buildEntity(WipReqInsDetailDO headerDO) {
        ReqInstructionDetailEntity headerEntity = ReqInstructionDetailEntity.get();
        BeanUtils.copyProperties(headerDO, headerEntity);
        headerEntity.setInstructionDetailId(headerDO.getInsDId())
                .setInstructionHeaderId(headerDO.getInsHId())
                .setSourceChangeDetailId(headerDO.getSourceCnDetailId());
        return headerEntity;
    }

    public static WipReqInsDetailDO buildDO(ReqInstructionDetailEntity headerEntity) {
        WipReqInsDetailDO headerDO = new WipReqInsDetailDO();
        BeanUtils.copyProperties(headerEntity, headerDO);
        headerDO.setInsDId(headerEntity.getInstructionDetailId());
        headerDO.setInsHId(headerEntity.getInstructionHeaderId());
        headerDO.setSourceCnDetailId(headerEntity.getSourceChangeDetailId());
        return headerDO;
    }

    public static List<ReqInstructionDetailEntity> batchBuildEntity(List<WipReqInsDetailDO> headerDOList) {
        List<ReqInstructionDetailEntity> headerEntityList = new ArrayList<>();
        for (WipReqInsDetailDO headerDO : headerDOList) {
            ReqInstructionDetailEntity headerEntity = buildEntity(headerDO);
            headerEntityList.add(headerEntity);
        }
        return headerEntityList;
    }

    public static List<WipReqInsDetailDO> batchBuildDO(List<ReqInstructionDetailEntity> headerEntityList) {
        List<WipReqInsDetailDO> headerDOList = new ArrayList<>();
        for (ReqInstructionDetailEntity headerEntity : headerEntityList) {
            WipReqInsDetailDO headerDO = new WipReqInsDetailDO();
            BeanUtils.copyProperties(headerEntity, headerDO);
            headerDOList.add(headerDO);
        }
        return headerDOList;
    }


}
