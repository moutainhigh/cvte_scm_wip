package com.cvte.scm.wip.infrastructure.item.mapper.dataobject;

import com.cvte.scm.wip.domain.common.item.entity.MdItemEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Table;

/**
 * @version 1.0
 * @descriptions: 仅仅用作查询物料信息
 * @author: ykccchen
 * @date: 2020/7/24 2:07 下午
 */
@Data
@Table(name = "scm.md_item")
@Accessors(chain = true)
public class MdItemDO {

    @ApiModelProperty("物料ID")
    @Column(name = "inventory_item_id")
    private String inventoryItemId;

    @ApiModelProperty("物料编号")
    @Column(name = "item_code")
    private String itemCode;

    @ApiModelProperty("机型")
    @Column(name = "part_family")
    private String partFamily;

    @ApiModelProperty("物料描述")
    @Column(name = "item_desc")
    private String itemDesc;

    @ApiModelProperty("物料状态")
    @Column(name = "inventory_item_status_code")
    private String inventoryItemStatusCode;

    public static MdItemEntity buildEntity(MdItemDO issuedDO) {
        MdItemEntity issuedEntity = new MdItemEntity();
        BeanUtils.copyProperties(issuedDO, issuedEntity);
        return issuedEntity;
    }

    public static MdItemDO buildDO(MdItemEntity issuedEntity) {
        MdItemDO issuedDO = new MdItemDO();
        BeanUtils.copyProperties(issuedEntity, issuedDO);
        return issuedDO;
    }
}
