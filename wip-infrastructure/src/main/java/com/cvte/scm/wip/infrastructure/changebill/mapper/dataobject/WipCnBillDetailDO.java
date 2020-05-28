package com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject;

import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillDetailEntity;
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
 * ${table.comment}
 *
 * @author author
 * @since 2020-05-22
 */
@Table(name = "wip_cn_bill_d")
@ApiModel(description = "${table.comment}")
@Data
@EqualsAndHashCode
public class WipCnBillDetailDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 更改单ID
     */
    @Id
    @Column(name = "bill_id")
    @ApiModelProperty(value = "更改单ID")
    private String billId;
    /**
     * 更改明细ID
     */
    @Column(name = "detail_id")
    @ApiModelProperty(value = "更改明细ID")
    private String detailId;
    /**
     * 生产批次号
     */
    @Column(name = "mo_lot_no")
    @ApiModelProperty(value = "生产批次号")
    private String moLotNo;
    /**
     * 组织ID
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "组织ID")
    private String organizationId;
    /**
     * 状态
     */
    @Column(name = "status")
    @ApiModelProperty(value = "状态")
    private String status;
    /**
     * 物料ID_旧
     */
    @Column(name = "item_id_old")
    @ApiModelProperty(value = "物料ID_旧")
    private String itemIdOld;
    /**
     * 物料ID_新
     */
    @Column(name = "item_id_new")
    @ApiModelProperty(value = "物料ID_新")
    private String itemIdNew;
    /**
     * 工序
     */
    @Column(name = "wkp_no")
    @ApiModelProperty(value = "工序")
    private String wkpNo;
    /**
     * 操作类型
     */
    @Column(name = "operation_type")
    @ApiModelProperty(value = "操作类型")
    private String operationType;
    /**
     * 位号
     */
    @Column(name = "pos_no")
    @ApiModelProperty(value = "位号")
    private String posNo;
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
     * 来源行ID
     */
    @Column(name = "source_line_id")
    @ApiModelProperty(value = "来源行ID")
    private String sourceLineId;

    public static ChangeBillDetailEntity buildEntity(WipCnBillDetailDO detailDO) {
        ChangeBillDetailEntity detailEntity = ChangeBillDetailEntity.get();
        BeanUtils.copyProperties(detailDO, detailEntity);
        return detailEntity;
    }

    public static WipCnBillDetailDO buildDO(ChangeBillDetailEntity detailEntity) {
        WipCnBillDetailDO detailDO = new WipCnBillDetailDO();
        BeanUtils.copyProperties(detailEntity, detailDO);
        return detailDO;
    }

    public static List<ChangeBillDetailEntity> batchBuildEntity(List<WipCnBillDetailDO> detailDOList) {
        List<ChangeBillDetailEntity> detailEntityList = new ArrayList<>();
        for (WipCnBillDetailDO detailDO : detailDOList) {
            ChangeBillDetailEntity detailEntity = buildEntity(detailDO);
            detailEntityList.add(detailEntity);
        }
        return detailEntityList;
    }

    public static List<WipCnBillDetailDO> batchBuildDO(List<ChangeBillDetailEntity> detailEntityList) {
        List<WipCnBillDetailDO> detailDOList = new ArrayList<>();
        for (ChangeBillDetailEntity detailEntity : detailEntityList) {
            WipCnBillDetailDO detailDO = new WipCnBillDetailDO();
            BeanUtils.copyProperties(detailEntity, detailDO);
            detailDOList.add(detailDO);
        }
        return detailDOList;
    }

}
