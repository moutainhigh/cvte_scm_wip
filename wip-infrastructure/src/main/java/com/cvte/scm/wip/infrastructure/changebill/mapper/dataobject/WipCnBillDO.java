package com.cvte.scm.wip.infrastructure.changebill.mapper.dataobject;

import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
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
@Table(name = "wip_cn_bill")
@ApiModel(description = "${table.comment}")
@Data
@EqualsAndHashCode
public class WipCnBillDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 更改单ID
     */
    @Id
    @Column(name = "bill_id")
    @ApiModelProperty(value = "更改单ID")
    private String billId;
    /**
     * 更改单号
     */
    @Column(name = "bill_no")
    @ApiModelProperty(value = "更改单号")
    private String billNo;
    /**
     * 组织ID
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "组织ID")
    private String organizationId;
    /**
     * 更改单类型
     */
    @Column(name = "bill_type")
    @ApiModelProperty(value = "更改单类型")
    private String billType;
    /**
     * 目标工单ID
     */
    @Column(name = "mo_id")
    @ApiModelProperty(value = "目标工单ID")
    private String moId;
    /**
     * 更改单状态
     */
    @Column(name = "bill_status")
    @ApiModelProperty(value = "更改单状态")
    private String billStatus;
    /**
     * 生效时间
     */
    @Column(name = "enable_date")
    @ApiModelProperty(value = "生效时间")
    private Date enableDate;
    /**
     * ${field.comment}
     */
    @Column(name = "disable_date")
    @ApiModelProperty(value = "${field.comment}")
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
     * 版本
     */
    @Column(name = "version")
    @ApiModelProperty(value = "版本")
    private Long version;
    /**
     * 更改原因
     */
    @Column(name = "change_content")
    @ApiModelProperty(value = "更改原因")
    private String changeContent;
    /**
     * 备注
     */
    @Column(name = "remarks")
    @ApiModelProperty(value = "备注")
    private String remarks;
    /**
     * 生管备注
     */
    @Column(name = "pc_remarks")
    @ApiModelProperty(value = "生管备注")
    private String pcRemarks;
    /**
     * 目标生产批次
     */
    @Column(name = "mot_lot_no")
    @ApiModelProperty(value = "目标生产批次")
    private String motLotNo;
    /**
     * 源更改单需求单号
     */
    @Column(name = "source_no")
    @ApiModelProperty(value = "源更改单需求单号")
    private String sourceNo;
    /**
     * 改后生产批次
     */
    @Column(name = "to_mo_lot_no")
    @ApiModelProperty(value = "改后生产批次")
    private String toMoLotNo;
    /**
     * 工厂ID
     */
    @Column(name = "factory_id")
    @ApiModelProperty(value = "工厂ID")
    private String factoryId;
    /*
     * 改前批次状态(1:未发放 3:已发放 4:完成 7:已取消 12:已关闭)
     */
    @Column(name = "status_type")
    @ApiModelProperty(value = "改前批次状态")
    private String statusType;
    /*
     * 更改单类型(1:库存 2:上线 3:未上线)
     */
    @Column(name = "type_code")
    @ApiModelProperty(value = "更改单类型")
    private String typeCode;

    public static ChangeBillEntity buildEntity(WipCnBillDO headerDO) {
        ChangeBillEntity headerEntity = ChangeBillEntity.get();
        BeanUtils.copyProperties(headerDO, headerEntity);
        return headerEntity;
    }

    public static WipCnBillDO buildDO(ChangeBillEntity headerEntity) {
        WipCnBillDO headerDO = new WipCnBillDO();
        BeanUtils.copyProperties(headerEntity, headerDO);
        return headerDO;
    }

    public static List<ChangeBillEntity> batchBuildEntity(List<WipCnBillDO> headerDOList) {
        List<ChangeBillEntity> headerEntityList = new ArrayList<>();
        for (WipCnBillDO headerDO : headerDOList) {
            ChangeBillEntity headerEntity = buildEntity(headerDO);
            headerEntityList.add(headerEntity);
        }
        return headerEntityList;
    }

    public static List<WipCnBillDO> batchBuildDO(List<ChangeBillEntity> headerEntityList) {
        List<WipCnBillDO> headerDOList = new ArrayList<>();
        for (ChangeBillEntity headerEntity : headerEntityList) {
            WipCnBillDO headerDO = new WipCnBillDO();
            BeanUtils.copyProperties(headerEntity, headerDO);
            headerDOList.add(headerDO);
        }
        return headerDOList;
    }

}
