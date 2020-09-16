package com.cvte.scm.wip.domain.core.patch.entity;

import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @version 1.0
 * @descriptions: 投料单状态表
 * @author: ykccchen
 * @date: 2020/7/24 11:02 上午
 */
@Data
@Table(name = "wip_patch_wf")
@Accessors(chain = true)
public class WipPatchWfEntity extends BaseModel {

    @ApiModelProperty("自增ID主键")
    @Id
    private Integer id;

    @ApiModelProperty("补料单ID")
    private String billId;

    @ApiModelProperty("状态")
    private String status;


    @ApiModelProperty("操作人")
    private String crtUser;

    @ApiModelProperty("操作时间")
    private Date crtDate;
}
