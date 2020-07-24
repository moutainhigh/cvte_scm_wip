package com.cvte.scm.wip.domain.core.ckd.dto.view;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @author zy
 * @date 2020-05-13 19:51
 **/
@Data
public class WipMcTaskView {


    /**
     * 主键
     */
    @ApiModelProperty(value = "主键")
    private String mcTaskId;
    /**
     * 配料任务编号
     */
    @ApiModelProperty(value = "配料任务编号")
    private String mcTaskNo;

    @ApiModelProperty(value = "配料任务编号")
    private String mcWfId;

    /**
     * 工厂
     */
    @ApiModelProperty(value = "工厂")
    private String factoryId;
    /**
     * 客户
     */
    @ApiModelProperty(value = "客户")
    private String client;
    /**
     * 齐套日期
     */
    @ApiModelProperty(value = "齐套日期")
    private Date mtrReadyTime;
    /**
     * 备注
     */
    @ApiModelProperty(value = "备注")
    private String remark;

    private String status;

    private String sourceLineId;
    private Date crtTime;

}
