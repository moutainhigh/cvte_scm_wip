package com.cvte.scm.wip.domain.core.ckd.dto;

import com.cvte.csb.sys.common.MyBaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-05-14 10:37
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class WipMcTaskUnlockDTO extends MyBaseEntity {

    private String optUser;

    private List<String> sourceLineIds;
}
