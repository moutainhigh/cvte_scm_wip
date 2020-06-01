package com.cvte.scm.wip.domain.core.requirement.valueobject;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-30 09:50
 **/
@Data
@Accessors(chain = true)
public class QueryWipReqHeaderVO {

    private List<String> wipHeaderIds;
}
