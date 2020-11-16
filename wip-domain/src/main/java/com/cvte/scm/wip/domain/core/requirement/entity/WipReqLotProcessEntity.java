package com.cvte.scm.wip.domain.core.requirement.entity;

import com.cvte.csb.validator.vtor.annotation.NotBlankNull;
import com.cvte.scm.wip.common.utils.BatchProcessUtils;
import com.cvte.scm.wip.domain.common.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/4 10:47
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@EqualsAndHashCode(callSuper = false)
@Data
@Component
@Accessors(chain = true)
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class WipReqLotProcessEntity extends BaseModel {

    private String id;

    @NotBlankNull(message = "业务组织不可为空")
    private String organizationId;

    @NotBlankNull(message = "工单ID不可为空")
    private String wipEntityId;

    @NotBlankNull(message = "物料不可为空")
    private String itemId;

    private String itemNo;

    @NotBlankNull(message = "工序不可为空")
    private String wkpNo;

    @NotBlankNull(message = "领料批次不可为空")
    private String mtrLotNo;

    private Long issuedQty;

    private String lockStatus;

    private String lockMode;

    private String processStatus;

    private String processResult;

    @NotBlankNull(message = "来源系统不可为空")
    private String sourceCode;

    @NotBlankNull(message = "操作用户不可为空")
    private String optUser;

    private String crtUser;

    private Date crtDate;

    private String updUser;

    private Date updDate;

    public String getItemKey() {
        return BatchProcessUtils.getKey(this.organizationId, this.wipEntityId, this.itemNo, this.wkpNo);
    }

}
