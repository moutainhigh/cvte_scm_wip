package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.scm.wip.common.utils.BatchProcessUtils;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author yt
 * @since 2020-01-17
 */
@EqualsAndHashCode(callSuper = false)
@Data
@Accessors(chain = true)
public class WipReqLotIssuedEntity extends BaseModel {

    private String id;

    @ApiModelProperty(value = "投料单头ID")
    @NotBlank(message = "投料单头不可为空")
    private String headerId;

    @ApiModelProperty(value = "库存组织")
    @NotBlank(message = "库存组织不可为空")
    private String organizationId;

    @ApiModelProperty(value = "物料编码")
    @NotBlank(message = "物料编码不可为空")
    private String itemNo;

    @ApiModelProperty(value = "工序")
    @NotBlank(message = "工序不可为空")
    private String wkpNo;

    @ApiModelProperty(value = "领料批次")
    @NotBlank(message = "领料批次不可为空")
    private String mtrLotNo;

    @ApiModelProperty(value = "领料数量")
    private Long issuedQty;

    @ApiModelProperty(value = "状态")
    private String status;

    private String rmk01;

    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;

    private String crtUser;

    private Date crtDate;

    private String updUser;

    private Date updDate;

    @ApiModelProperty(value = "锁定状态")
    private String lockStatus;

    @ApiModelProperty(value = "未发料数量")
    private BigDecimal unissuedQty;

    @ApiModelProperty(value = "锁定类型")
    private String lockType;

    @ApiModelProperty(value = "过账数量")
    private BigDecimal postQty;

    @ApiModelProperty(value = "分配数量")
    @Min(value = 0, message = "最小领料数量为0")
    private BigDecimal assignQty;

    public String getItemKey() {
        return BatchProcessUtils.getKey(this.organizationId, this.headerId, this.itemNo, this.wkpNo);
    }

    public static WipReqLotIssuedEntity buildForPost(String organizationId, String headerId, String itemNo, String wkpNo, BigDecimal postQty) {
        WipReqLotIssuedEntity lotIssued = new WipReqLotIssuedEntity();
        lotIssued.setOrganizationId(organizationId)
                .setHeaderId(headerId)
                .setItemNo(itemNo)
                .setWkpNo(wkpNo)
                .setPostQty(postQty);
        return lotIssued;
    }

    public static Map<String, WipReqLotIssuedEntity> toLotMap(List<WipReqLotIssuedEntity> reqLotIssuedList) {
        return reqLotIssuedList.stream().collect(Collectors.toMap(WipReqLotIssuedEntity::getMtrLotNo, Function.identity()));
    }

}