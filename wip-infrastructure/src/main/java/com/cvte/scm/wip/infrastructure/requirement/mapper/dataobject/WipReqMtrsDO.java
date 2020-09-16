package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.cvte.scm.wip.domain.core.requirement.entity.WipReqMtrsEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Column;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author jf
 * @since 2020-03-04
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Table(name = "wip.wip_req_mtrs")
@ApiModel(description = "物料信息表")
public class WipReqMtrsDO {

    @Column(name = "header_id")
    private String headerId;

    @Column(name = "organization_id")
    private String organizationId;

    @Column(name = "item_group")
    private String itemGroup;

    @Column(name = "wkp_no")
    private String wkpNo;

    @Column(name = "item_id")
    @ApiModelProperty(value = "${field.comment}")
    private String itemId;

    @Column(name = "item_no")
    @ApiModelProperty(value = "${field.comment}")
    private String itemNo;

    public static WipReqMtrsEntity buildEntity(WipReqMtrsDO issuedDO) {
        WipReqMtrsEntity issuedEntity = new WipReqMtrsEntity();
        BeanUtils.copyProperties(issuedDO, issuedEntity);
        return issuedEntity;
    }

    public static WipReqMtrsDO buildDO(WipReqMtrsEntity issuedEntity) {
        WipReqMtrsDO issuedDO = new WipReqMtrsDO();
        BeanUtils.copyProperties(issuedEntity, issuedDO);
        return issuedDO;
    }
    public static List<WipReqMtrsEntity> buildEntityList(List<WipReqMtrsDO> wipReqMtrsDOS){
        List<WipReqMtrsEntity> list = new ArrayList<>();
        wipReqMtrsDOS.forEach(v->{
            WipReqMtrsEntity issuedEntity = new WipReqMtrsEntity();
            BeanUtils.copyProperties(v, issuedEntity);
            list.add(issuedEntity);
        });
        return list;
    }

}