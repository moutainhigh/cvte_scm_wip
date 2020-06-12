package com.cvte.scm.wip.domain.core.ckd.dto.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author zy
 * @date 2020-04-30 10:36
 **/
@Data
@Accessors(chain = true)
public class WipMcWfView {

    @ApiModelProperty(value = "主键")
    private String wfId;

    @ApiModelProperty(value = "配料任务ID")
    private String mcTaskId;
    /**
     * 状态（create:创建，verify：审批，reject：驳回，cancal:取消，confirm：工厂已确认，finish：完成，close：关闭）
     */
    @ApiModelProperty(value = "状态（create:创建，verify：审批，reject：驳回，cancal:取消，confirm：工厂已确认，finish：完成，close：关闭）")
    private String status;

}
