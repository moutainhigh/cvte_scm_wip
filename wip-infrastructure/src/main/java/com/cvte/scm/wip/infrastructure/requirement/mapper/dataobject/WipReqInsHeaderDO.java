package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;

import com.cvte.scm.wip.domain.core.requirement.entity.ReqInsEntity;
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
@Table(name = "wip_req_ins_h")
@ApiModel(description = "投料单指令集")
@Data
@EqualsAndHashCode
public class WipReqInsHeaderDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 指令集ID
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "ins_h_id")
    @ApiModelProperty(value = "指令集ID")
    private String insHId;
    /**
     * 更改单ID
     */
    @Column(name = "source_cn_bill_id")
    @ApiModelProperty(value = "更改单ID")
    private String sourceCnBillId;
    /**
     * 指令集状态
     */
    @Column(name = "status")
    @ApiModelProperty(value = "指令集状态")
    private String status;
    /**
     * 目标投料单ID
     */
    @Column(name = "aim_header_id")
    @ApiModelProperty(value = "目标投料单ID")
    private String aimHeaderId;
    /**
     * 目标批次号
     */
    @Column(name = "aim_req_lot_no")
    @ApiModelProperty(value = "目标批次号")
    private String aimReqLotNo;
    /**
     * 生效时间
     */
    @Column(name = "enable_date")
    @ApiModelProperty(value = "生效时间")
    private Date enableDate;
    /**
     * 失效时间
     */
    @Column(name = "disable_date")
    @ApiModelProperty(value = "失效时间")
    private Date disableDate;
    /**
     * 目标批次号
     */
    @Column(name = "confirmed_by")
    @ApiModelProperty(value = "确认人")
    private String confirmedBy;
    /**
     * 作废人
     */
    @Column(name = "invalid_by")
    @ApiModelProperty(value = "作废人")
    private String invalidBy;
    /**
     * 作废原因
     */
    @Column(name = "invalid_reason")
    @ApiModelProperty(value = "作废原因")
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

    public static ReqInsEntity buildEntity(WipReqInsHeaderDO headerDO) {
        ReqInsEntity headerEntity = ReqInsEntity.get();
        BeanUtils.copyProperties(headerDO, headerEntity);
        headerEntity.setInsHeaderId(headerDO.getInsHId())
                .setSourceChangeBillId(headerDO.getSourceCnBillId());
        return headerEntity;
    }

    public static WipReqInsHeaderDO buildDO(ReqInsEntity headerEntity) {
        WipReqInsHeaderDO headerDO = new WipReqInsHeaderDO();
        BeanUtils.copyProperties(headerEntity, headerDO);
        headerDO.setInsHId(headerEntity.getInsHeaderId());
        headerDO.setSourceCnBillId(headerEntity.getSourceChangeBillId());
        return headerDO;
    }

    public static List<ReqInsEntity> batchBuildEntity(List<WipReqInsHeaderDO> headerDOList) {
        List<ReqInsEntity> headerEntityList = new ArrayList<>();
        for (WipReqInsHeaderDO headerDO : headerDOList) {
            ReqInsEntity headerEntity = buildEntity(headerDO);
            headerEntityList.add(headerEntity);
        }
        return headerEntityList;
    }

    public static List<WipReqInsHeaderDO> batchBuildDO(List<ReqInsEntity> headerEntityList) {
        List<WipReqInsHeaderDO> headerDOList = new ArrayList<>();
        for (ReqInsEntity headerEntity : headerEntityList) {
            WipReqInsHeaderDO headerDO = new WipReqInsHeaderDO();
            BeanUtils.copyProperties(headerEntity, headerDO);
            headerDOList.add(headerDO);
        }
        return headerDOList;
    }

}
