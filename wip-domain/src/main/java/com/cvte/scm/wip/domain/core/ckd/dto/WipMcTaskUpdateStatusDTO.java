package com.cvte.scm.wip.domain.core.ckd.dto;

import com.cvte.csb.sys.common.MyBaseEntity;
import com.cvte.csb.validator.vtor.annotation.NotBlankNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author zy
 * @date 2020-04-30 18:11
 **/
@Data
@Accessors(chain = true)
@EqualsAndHashCode
public class WipMcTaskUpdateStatusDTO extends MyBaseEntity {

    private List<String> sourceLineIds;

    @NotBlankNull(message = "更新状态不能为空")
    private String updateToStatus;

    @NotBlankNull(message = "当前操作人")
    private String optUser;

}
