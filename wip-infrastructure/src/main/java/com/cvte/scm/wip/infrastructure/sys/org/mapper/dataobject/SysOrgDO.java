package com.cvte.scm.wip.infrastructure.sys.org.mapper.dataobject;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/10/8 09:55
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Table( name = "scm.sys_org")
@ApiModel(description = "事业部视图")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SysOrgDO extends BaseModel {

    @Id
    @Column(name = "id")
    @ApiModelProperty("ID")
    private String id;

    @Column(name = "org_name")
    @ApiModelProperty("组织名称")
    private String orgName;

    @Column(name = "abbr_name")
    @ApiModelProperty("组织简称")
    private String abbrName;

    @Column(name = "remark")
    @ApiModelProperty("备注")
    private String remark;

}
