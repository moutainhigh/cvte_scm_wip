package com.cvte.scm.wip.domain.common.audit.entity;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/10/10 09:05
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class WipAuditLogEntity extends BaseModel {

    private String id;

    private String batchId;

    private String model;

    private String entity;

    private String objectId;

    private String objectParentId;

    private String operationType;

    private String field;

    private String fieldName;

    private String before;

    private String after;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

}
