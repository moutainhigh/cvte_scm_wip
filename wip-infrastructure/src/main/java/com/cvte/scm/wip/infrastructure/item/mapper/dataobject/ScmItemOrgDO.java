package com.cvte.scm.wip.infrastructure.item.mapper.dataobject;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Table;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/19 11:23
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
@Table(name = "scm.md_item_org")
@EqualsAndHashCode(callSuper = false)
public class ScmItemOrgDO extends BaseModel {

    @Column(name = "ebs_organization_id")
    private String organizationId;

    @Column(name = "inventory_item_id")
    private String itemId;

    @Column(name = "item_code")
    private String itemNo;

    @Column(name = "lot_control_code")
    private String lotControlCode;

}
