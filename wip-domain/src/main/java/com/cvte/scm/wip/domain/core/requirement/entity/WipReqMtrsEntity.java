package com.cvte.scm.wip.domain.core.requirement.entity;


import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author jf
 * @since 2020-03-04
 */
@Data
@Accessors(chain = true)
public class WipReqMtrsEntity {

    private String headerId;

    private String organizationId;

    private String itemGroup;

    private String wkpNo;

    private String itemId;

    private String itemNo;

}