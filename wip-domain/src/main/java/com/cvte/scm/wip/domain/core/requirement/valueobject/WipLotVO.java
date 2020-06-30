package com.cvte.scm.wip.domain.core.requirement.valueobject;


import io.swagger.annotations.ApiModel;
import javax.persistence.*;
import com.cvte.csb.validator.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;


/**
 * ${table.comment}
 *
 * @author author
 * @since 2020-06-02
 */
@ApiModel(description = "工单关联(小)批次")
@Data
public class WipLotVO extends BaseEntity  {

    private String wipId;

    private String lotNumber;

    private BigDecimal lotQuantity;

    private String factoryId;
    
}
