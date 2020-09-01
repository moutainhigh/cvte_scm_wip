package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotIssuedEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yt
 * @since 2020-01-17
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@ApiModel(description = "投料单领料批次")
@Table(name = "wip.wip_req_lot_issued")
public class WipReqLotIssuedDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @Id
    @Column(name = "id")
    @ApiModelProperty(value = "ID")
    private String id;

    /**
     * 投料单头ID
     */
    @Column(name = "header_id")
    @ApiModelProperty(value = "投料单头ID")
    @NotBlank(message = "投料单头不可为空")
    private String headerId;
    /**
     * 库存组织
     */
    @Column(name = "organization_id")
    @ApiModelProperty(value = "库存组织")
    @NotBlank(message = "库存组织不可为空")
    private String organizationId;
    /**
     * 物料编码
     */
    @Column(name = "item_no")
    @ApiModelProperty(value = "物料编码")
    @NotBlank(message = "物料编码不可为空")
    private String itemNo;
    /**
     * 工序
     */
    @Column(name = "wkp_no")
    @ApiModelProperty(value = "工序")
    @NotBlank(message = "工序不可为空")
    private String wkpNo;
    /**
     * 领料批次
     */
    @Column(name = "mtr_lot_no")
    @ApiModelProperty(value = "领料批次")
    @NotBlank(message = "领料批次不可为空")
    private String mtrLotNo;
    /**
     * 领料数量
     */
    @Column(name = "issued_qty")
    @ApiModelProperty(value = "领料数量")
    @Min(value = 0, message = "最小领料数量为0")
    private Long issuedQty;
    /**
     * 状态
     */
    @Column(name = "status")
    @ApiModelProperty(value = "状态")
    private String status;
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
    /**
     * 锁定状态
     */
    @Column(name = "lock_status")
    @ApiModelProperty(value = "锁定状态")
    private String lockStatus;
    /**
     * 锁定类型
     */
    @Column(name = "lock_type")
    @ApiModelProperty(value = "锁定类型")
    private String lockType;

    public static WipReqLotIssuedEntity buildEntity(WipReqLotIssuedDO issuedDO) {
        WipReqLotIssuedEntity issuedEntity = new WipReqLotIssuedEntity();
        BeanUtils.copyProperties(issuedDO, issuedEntity);
        return issuedEntity;
    }

    public static WipReqLotIssuedDO buildDO(WipReqLotIssuedEntity issuedEntity) {
        WipReqLotIssuedDO issuedDO = new WipReqLotIssuedDO();
        BeanUtils.copyProperties(issuedEntity, issuedDO);
        return issuedDO;
    }

    public static List<WipReqLotIssuedEntity> batchBuildEntity(List<WipReqLotIssuedDO> issuedDOList) {
        List<WipReqLotIssuedEntity> lotIssuedEntityList = new ArrayList<>();
        for (WipReqLotIssuedDO lotIssuedDO : issuedDOList) {
            WipReqLotIssuedEntity issuedEntity = new WipReqLotIssuedEntity();
            BeanUtils.copyProperties(lotIssuedDO, issuedEntity);
            lotIssuedEntityList.add(issuedEntity);
        }
        return lotIssuedEntityList;
    }

}