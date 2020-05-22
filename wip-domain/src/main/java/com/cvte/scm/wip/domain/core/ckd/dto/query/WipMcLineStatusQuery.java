package com.cvte.scm.wip.domain.core.ckd.dto.query;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-13 16:19
 **/
@Data
@Accessors(chain = true)
public class WipMcLineStatusQuery {

    private List<String> sourceLineIds;

    private String sourceLineId;
}
