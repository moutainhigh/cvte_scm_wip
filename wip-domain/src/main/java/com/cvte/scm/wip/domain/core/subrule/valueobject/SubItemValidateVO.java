package com.cvte.scm.wip.domain.core.subrule.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/25 09:37
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class SubItemValidateVO {

    private String organizationId;

    private String beforeItemId;

    private String afterItemId;

    private String beforeItemNo;

    private String afterItemNo;

}
