package com.cvte.scm.wip.domain.core.scm.dto.query;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-11 17:40
 **/
@Data
@Accessors(chain = true)
public class SysOrgOrganizationVQuery {

    private List<String> ebsOrganizationIds;

}
