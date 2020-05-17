package com.cvte.scm.wip.domain.core.item.entity;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ScmItemEntity {

    private String organizationId;

    private String itemId;

    private String itemNo;

}