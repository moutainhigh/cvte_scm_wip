package com.cvte.scm.wip.infrastructure.patch.mapper.dataobject;


import com.cvte.scm.wip.domain.common.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.Date;


/**
 * ${table.comment}
 *
 * @author author
 * @since 2020-07-25
 */
@Table(name = "wip_patch_history")
@ApiModel(description = "${table.comment}")
@SequenceGenerator(sequenceName = "wip_patch_history_id_seq",name = "wip_patch_history_id_seq")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class WipPatchHistoryDO extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 自增ID
     */
    @Id
    //@GeneratedValue(generator = "wip_patch_history_id_seq",strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    @ApiModelProperty(value = "自增ID")
    private Integer id;
    /**
     * 补料单 ID
     */
    @Column(name = "bill_id")
    @ApiModelProperty(value = "补料单 ID")
    private String billId;
    /**
     * 物料ID
     */
    @Column(name = "item_id")
    @ApiModelProperty(value = "物料ID")
    private String itemId;
    /**
     * 物料编码
     */
    @Column(name = "item_code")
    @ApiModelProperty(value = "物料编码")
    private String itemCode;
    /**
     * ${field.comment}
     */
    @ApiModelProperty(value = "${field.comment}")
    private String history;

    /**
     * ${field.comment}
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "更改人")
    private String crtUser;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_date")
    @ApiModelProperty(value = "更改时间")
    private Date crtDate;
}
