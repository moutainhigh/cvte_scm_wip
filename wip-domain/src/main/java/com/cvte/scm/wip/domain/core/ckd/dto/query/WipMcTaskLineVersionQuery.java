package com.cvte.scm.wip.domain.core.ckd.dto.query;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zy
 * @date 2020-04-29 16:41
 **/
@Data
@Accessors(chain = true)
public class WipMcTaskLineVersionQuery {

    private String versionId;

    private String lineStatus;
}
