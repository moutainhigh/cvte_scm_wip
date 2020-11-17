package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLogEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author jf
 * @since 2020-01-01
 */
@Data
@Accessors(chain = true)
@Table(name = "wip.wip_req_log")
@ApiModel(description = "投料明细日志表，主要记录投料明细数据修改前后的物料编码。")
public class WipReqLogDO {

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "log_id")
    @ApiModelProperty(value = "${field.comment}")
    private String logId;

    @Column(name = "header_id")
    @ApiModelProperty(value = "${field.comment}")
    private String headerId;
    /**
     * ${field.comment}
     */
    @Column(name = "line_id")
    @ApiModelProperty(value = "${field.comment}")
    private String lineId;

    @Column(name = "before_item_no")
    @ApiModelProperty(value = "修改前物料编码")
    private String beforeItemNo;

    @Column(name = "after_item_no")
    @ApiModelProperty(value = "修改后物料编码")
    private String afterItemNo;

    @Column(name = "before_item_qty")
    @ApiModelProperty(value = "修改前物料数量")
    private Long beforeItemQty;

    @Column(name = "after_item_qty")
    @ApiModelProperty(value = "修改后物料数量")
    private Long afterItemQty;

    @Column(name = "before_wkp_no")
    @ApiModelProperty(value = "修改前工序号")
    private String beforeWkpNo;

    @Column(name = "after_wkp_no")
    @ApiModelProperty(value = "修改后工序号")
    private String afterWkpNo;

    @Column(name = "before_pos_no")
    @ApiModelProperty(value = "修改前位号")
    private String beforePosNo;

    @Column(name = "after_pos_no")
    @ApiModelProperty(value = "修改后位号")
    private String afterPosNo;

    @Column(name = "operation_type")
    @ApiModelProperty(value = "操作类型")
    private String operationType;
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
    /**
     * 领料批次
     */
    @Column(name = "mtr_lot_no")
    @ApiModelProperty(value = "领料批次")
    private String mtrLotNo;
    /**
     * 领料批次id
     */
    @Column(name = "rmk01")
    @ApiModelProperty(value = "领料批次id")
    private String rmk01;
    /**
     * ${field.comment}
     */
    @Column(name = "rmk02")
    @ApiModelProperty(value = "${field.comment}")
    private String rmk02;
    /**
     * ${field.comment}
     */
    @Column(name = "rmk03")
    @ApiModelProperty(value = "${field.comment}")
    private String rmk03;
    /**
     * ${field.comment}
     */
    @Column(name = "rmk04")
    @ApiModelProperty(value = "${field.comment}")
    private String rmk04;
    /**
     * ${field.comment}
     */
    @Column(name = "rmk05")
    @ApiModelProperty(value = "${field.comment}")
    private String rmk05;

    public static WipReqLogEntity buildEntity(WipReqLogDO issuedDO) {
        WipReqLogEntity issuedEntity = new WipReqLogEntity();
        BeanUtils.copyProperties(issuedDO, issuedEntity);
        return issuedEntity;
    }

    public static WipReqLogDO buildDO(WipReqLogEntity issuedEntity) {
        WipReqLogDO issuedDO = new WipReqLogDO();
        BeanUtils.copyProperties(issuedEntity, issuedDO);
        return issuedDO;
    }

}