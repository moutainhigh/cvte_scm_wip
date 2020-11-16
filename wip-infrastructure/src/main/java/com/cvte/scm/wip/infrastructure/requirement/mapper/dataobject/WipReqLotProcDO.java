package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * 投料单领料批次处理
 *
 * @author author
 * @since 2020-09-04
 */
@Table(name = "wip_req_lot_proc")
@ApiModel(description = "投料单领料批次处理")
@Data
@EqualsAndHashCode(callSuper = false)
public class WipReqLotProcDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty(value = "id")
    private String id;
    /**
     * 业务组织ID
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "业务组织ID")
    private String organizationId;
    /**
     * 工单ID
     */
    @Column(name = "wip_entity_id")
    @ApiModelProperty(value = "工单ID")
    private String wipEntityId;
    /**
     * 物料ID
     */
    @Column(name = "item_id")
    @ApiModelProperty(value = "物料ID")
    private String itemId;
    /**
     * 物料编码
     */
    @Column(name = "item_no")
    @ApiModelProperty(value = "物料编码")
    private String itemNo;
    /**
     * 工序
     */
    @Column(name = "wkp_no")
    @ApiModelProperty(value = "工序")
    private String wkpNo;
    /**
     * 领料批次
     */
    @Column(name = "mtr_lot_no")
    @ApiModelProperty(value = "领料批次")
    private String mtrLotNo;
    /**
     * 领料数量
     */
    @Column(name = "issued_qty")
    @ApiModelProperty(value = "领料数量")
    private String issuedQty;
    /**
     * 批次锁定状态
     */
    @Column(name = "lock_status")
    @ApiModelProperty(value = "批次锁定状态")
    private String lockStatus;
    /**
     * 批次锁定模式
     */
    @Column(name = "lock_mode")
    @ApiModelProperty(value = "批次锁定模式")
    private String lockMode;
    /**
     * 数据处理状态
     */
    @Column(name = "process_status")
    @ApiModelProperty(value = "数据处理状态")
    private String processStatus;
    /**
     * 处理结果
     */
    @Column(name = "process_result")
    @ApiModelProperty(value = "处理结果")
    private String processResult;
    /**
     * 来源系统
     */
    @Column(name = "source_code")
    @ApiModelProperty(value = "来源系统")
    private String sourceCode;
    /**
     * 创建人
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "创建人")
    private String crtUser;
    /**
     * 创建时间
     */
    @Column(name = "crt_date")
    @ApiModelProperty(value = "创建时间")
    private Date crtDate;
    /**
     * 更新人
     */
    @Column(name = "upd_user")
    @ApiModelProperty(value = "更新人")
    private String updUser;
    /**
     * 更新时间
     */
    @Column(name = "upd_date")
    @ApiModelProperty(value = "更新时间")
    private Date updDate;

}
