package com.cvte.scm.wip.domain.core.changebill.valueobject;

import lombok.Data;

import java.util.Date;

/**
  * 获取更改单的参数值对象
  * @author  : xueyuting
  * @since    : 2020/5/21 11:40
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class ChangeBillQueryVO {

    private String organizationId;

    private Date lastUpdDate;

    private String factoryId;

}
