package com.cvte.scm.wip.domain.core.subrule.valueobject;

import lombok.Data;

import java.io.Serializable;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/2/22 11:20
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
public class AuditorVO implements Serializable {
    /**
     * 账号
     */
    private String LoginName;
    /**
     * 姓名
     */
    private String name;
}
