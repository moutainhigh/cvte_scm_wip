package com.cvte.scm.wip.domain.core.ckd.dto.query;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-04-30 09:55
 **/
@Data
@Accessors(chain = true)
public class WipMcTaskQuery {

    private List<String> taskIds;

    private List<String> mcTaskNos;

    private List<String> sourceLineIds;

    private String status;

}
