package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.cvte.csb.validator.entity.BaseEntity;
import com.cvte.scm.wip.domain.core.requirement.entity.WipEbsReqLogEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * EBS投料单的操作记录
 *
 * @author author
 * @since 2020-03-12
 */
@Table(name = "wip_ebs_req_log")
@ApiModel(description = "EBS投料单的操作记录")
@Data
@EqualsAndHashCode(callSuper = true)
public class WipEbsReqLogDO extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "log_id")
    @ApiModelProperty(value = "${field.comment}")
    private String logId;
    /**
     * 操作的表
     */
    @Column(name = "op_table")
    @ApiModelProperty(value = "操作的表")
    private String opTable;
    /**
     * 操作类型
     */
    @Column(name = "op_type")
    @ApiModelProperty(value = "操作类型")
    private String opType;
    /**
     * ${field.comment}
     */
    @Column(name = "op_pos")
    @ApiModelProperty(value = "${field.comment}")
    private String opPos;
    /**
     * ${field.comment}
     */
    @Column(name = "op_ts")
    @ApiModelProperty(value = "${field.comment}")
    private String opTs;
    /**
     * 请求时间
     */
    @Column(name = "request_ts")
    @ApiModelProperty(value = "请求时间")
    private String requestTs;
    /**
     * 库存组织
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "库存组织")
    private Integer organizationId;
    /**
     * 对应投料单header_id
     */
    @Column(name = "wip_entity_id")
    @ApiModelProperty(value = "对应投料单header_id")
    private Integer wipEntityId;
    /**
     * 工序
     */
    @Column(name = "operation_seq_num")
    @ApiModelProperty(value = "工序")
    private Integer operationSeqNum;
    /**
     * 改前物料ID
     */
    @Column(name = "before_item_id")
    @ApiModelProperty(value = "改前物料ID")
    private Integer beforeItemId;
    /**
     * 改后物料ID
     */
    @Column(name = "after_item_id")
    @ApiModelProperty(value = "改后物料ID")
    private Integer afterItemId;
    /**
     * 改前投料详情
     */
    @ApiModelProperty(value = "改前投料详情")
    private String before;
    /**
     * 改后投料详情
     */
    @ApiModelProperty(value = "改后投料详情")
    private String after;
    /**
     * 处理状态
     */
    @Column(name = "process_status")
    @ApiModelProperty(value = "处理状态")
    private String processStatus;
    /**
     * 成功处理后的分组标识
     */
    @Column(name = "group_id")
    @ApiModelProperty(value = "成功处理后的分组标识")
    private String groupId;
    /**
     * 异常原因
     */
    @Column(name = "exception_reason")
    @ApiModelProperty(value = "异常原因")
    private String exceptionReason;
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

    public static WipEbsReqLogEntity buildEntity(WipEbsReqLogDO ebsReqLogDO) {
        WipEbsReqLogEntity ebsReqLogEntity = new WipEbsReqLogEntity();
        BeanUtils.copyProperties(ebsReqLogDO, ebsReqLogEntity);
        return ebsReqLogEntity;
    }

    public static WipEbsReqLogDO buildDO(WipEbsReqLogEntity ebsReqLogEntity) {
        WipEbsReqLogDO ebsReqLogDO = new WipEbsReqLogDO();
        BeanUtils.copyProperties(ebsReqLogEntity, ebsReqLogDO);
        return ebsReqLogDO;
    }

    public static List<WipEbsReqLogEntity> batchBuildEntity(List<WipEbsReqLogDO> ebsReqLogDOList) {
        List<WipEbsReqLogEntity> ebsReqLogEntityList = new ArrayList<>();
        for (WipEbsReqLogDO ebsReqLogDO : ebsReqLogDOList) {
            WipEbsReqLogEntity ebsReqLogEntity = buildEntity(ebsReqLogDO);
            ebsReqLogEntityList.add(ebsReqLogEntity);
        }
        return ebsReqLogEntityList;
    }

}