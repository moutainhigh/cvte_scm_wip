package com.cvte.scm.wip.infrastructure.ckd.mapper.dataobject;


import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.domain.core.ckd.entity.WipMcInoutStockEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * ${table.comment}
 *
 * @author zy
 * @since 2020-04-28
 */
@Table(name = "wip_mc_req_to_task")
@ApiModel(description = "${table.comment}")
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipMcReqToTaskDO {

    /**
     * ${field.comment}
     */
    @Id
    @GeneratedValue(generator = "UUID")
    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * ${field.comment}
     */
    @Column(name = "mc_req_id")
    @ApiModelProperty(value = "${field.comment}")
    private String mcReqId;
    /**
     * ${field.comment}
     */
    @Column(name = "mc_task_id")
    @ApiModelProperty(value = "${field.comment}")
    private String mcTaskId;



    public static WipMcInoutStockEntity buildEntity(WipMcInoutStockDO inoutStockDO) {
        if (ObjectUtils.isNull(inoutStockDO)) {
            return null;
        }
        WipMcInoutStockEntity inoutStockEntity = new WipMcInoutStockEntity();
        BeanUtils.copyProperties(inoutStockDO, inoutStockEntity);
        return inoutStockEntity;
    }

    public static WipMcInoutStockDO buildDO(WipMcInoutStockEntity stockEntity) {
        if (ObjectUtils.isNull(stockEntity)) {
            return null;
        }

        WipMcInoutStockDO inoutStockDO = new WipMcInoutStockDO();
        BeanUtils.copyProperties(stockEntity, inoutStockDO);
        return inoutStockDO;
    }

}
