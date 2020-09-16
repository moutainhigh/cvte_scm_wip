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
 * @descriptions: 投料单历史记录表
 * @author: ykccchen
 * @date: 2020/7/24 11:02 上午
 */
@Data
@Table(name = "wip_patch_history")
@Accessors(chain = true)
public class WipPatchHistoryEntity extends BaseModel {

    @ApiModelProperty("自增ID主键")
    @Id
    private Integer id;

    @ApiModelProperty("采购单ID")
    private String billId;

    @ApiModelProperty("物料ID")
    private String itemId;

    @ApiModelProperty("物料编号")
    private String itemCode;

    @ApiModelProperty("历史记录")
    private String history;

    @ApiModelProperty(value = "更改人")
    private String crtUser;

    @ApiModelProperty(value = "更改时间")
    private Date crtDate;
}
