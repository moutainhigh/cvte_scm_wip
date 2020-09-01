package com.cvte.scm.wip.domain.core.requirement.valueobject;

import com.cvte.csb.sys.common.MyBaseEntity;
import com.cvte.csb.validator.vtor.annotation.NotBlankNull;
import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.requirement.entity.WipReqLotProcessEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/3 20:06
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipReqLotProcessVO extends MyBaseEntity implements VO {

    @NotBlankNull(message = "工单ID不可为空")
    private String wipEntityId;

    @NotBlankNull(message = "业务组织不可为空")
    private String organizationId;

    @NotBlankNull(message = "物料不可为空")
    private String itemId;

    private String itemNo;

    @NotBlankNull(message = "工序不可为空")
    private String wkpNo;

    @NotBlankNull(message = "领料批次不可为空")
    private String mtrLotNo;

    private Long issuedQty;

    @NotBlankNull(message = "来源系统不可为空")
    private String sourceCode;

    @NotBlankNull(message = "操作用户不可为空")
    private String optUser;

    public static WipReqLotProcessVO build(WipReqLotProcessEntity lotProcessEntity) {
        WipReqLotProcessVO vo = new WipReqLotProcessVO();
        vo.setWipEntityId(lotProcessEntity.getWipEntityId())
                .setOrganizationId(lotProcessEntity.getOrganizationId())
                .setItemId(lotProcessEntity.getItemId())
                .setItemNo(lotProcessEntity.getItemNo())
                .setWkpNo(lotProcessEntity.getWkpNo())
                .setMtrLotNo(lotProcessEntity.getMtrLotNo())
                .setIssuedQty(lotProcessEntity.getIssuedQty());
        return vo;
    }

}
