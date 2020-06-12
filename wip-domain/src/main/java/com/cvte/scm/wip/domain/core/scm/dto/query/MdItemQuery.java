package com.cvte.scm.wip.domain.core.scm.dto.query;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-06-03 20:27
 **/
@Data
@Accessors(chain = true)
public class MdItemQuery {

    private List<String> itemCodes;

}
