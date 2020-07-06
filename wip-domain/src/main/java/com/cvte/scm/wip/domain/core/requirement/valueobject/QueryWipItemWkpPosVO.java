package com.cvte.scm.wip.domain.core.requirement.valueobject;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;

/**
 * @author zy
 * @date 2020-05-25 16:45
 **/
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class QueryWipItemWkpPosVO {

    private List<String> itemCodes;

    private List<String> productModels;

    private List<String> organizationIds;

    private Date queryDate;

}
