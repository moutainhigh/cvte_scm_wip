package com.cvte.scm.wip.domain.core.item.entity;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/19 11:32
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class ScmItemOrgEntity extends BaseModel {

    private String organizationId;

    private String itemId;

    private String itemNo;

    private String lotControlCode;

}
