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
@Table(name = "scm.scm_item_v")
public class ScmItemDO {

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "item_id")
    private String itemId;

    @Column(name = "item_no")
    private String itemNo;

    public static ScmItemEntity buildEntity(ScmItemDO issuedDO) {
        ScmItemEntity issuedEntity = new ScmItemEntity();
        BeanUtils.copyProperties(issuedDO, issuedEntity);
        return issuedEntity;
    }

    public static ScmItemDO buildDO(ScmItemEntity issuedEntity) {
        ScmItemDO issuedDO = new ScmItemDO();
        BeanUtils.copyProperties(issuedEntity, issuedDO);
        return issuedDO;
    }

}