package com.cvte.scm.wip.domain.core.ckd.dto.query;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-04-30 10:38
 **/
@Data
@Accessors(chain = true)
public class WipMcWfQuery {

    private List<String> wfIds;

    private String mcTaskId;
}
