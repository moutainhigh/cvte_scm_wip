package com.cvte.scm.wip.infrastructure.item.mapper.dataobject;

import com.cvte.scm.wip.domain.core.item.entity.ScmItemEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Table;

@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Table(name = "scm.md_item")
public class ScmItemDO {

    @Column(name = "inventory_item_id")
    private String itemId;

    @Column(name = "item_code")
    private String itemNo;

    public static ScmItemEntity buildEntity(ScmItemDO issuedDO, String organizationId) {
        ScmItemEntity itemEntity = new ScmItemEntity();
        BeanUtils.copyProperties(issuedDO, itemEntity);
        itemEntity.setOrganizationId(organizationId);
        return itemEntity;
    }

    public static ScmItemDO buildDO(ScmItemEntity issuedEntity) {
        ScmItemDO issuedDO = new ScmItemDO();
        BeanUtils.copyProperties(issuedEntity, issuedDO);
        return issuedDO;
    }

}