package com.cvte.scm.wip.domain.core.scm.vo;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zy
 * @date 2020-05-11 17:40
 **/
@Data
@Accessors(chain = true)
public class SysOrgOrganizationVO {

    private String dimensionId;

    private String orgId;

    private String relationCode;

    private String orgType;

    private String orgName;

    private String abbrName;

    private String soOrgId;

    private String ebsOrganizationCode;

    private String ebsOrganizationId;

    private String ebsOrgId;

}
