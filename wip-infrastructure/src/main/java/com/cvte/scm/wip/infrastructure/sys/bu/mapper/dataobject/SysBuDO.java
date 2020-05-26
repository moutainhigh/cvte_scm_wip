package com.cvte.scm.wip.infrastructure.sys.bu.mapper.dataobject;

import com.cvte.csb.validator.entity.BaseEntity;
import com.cvte.scm.wip.domain.common.bu.entity.SysBuEntity;
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
  * 
  * @author  : xueyuting
  * @since    : 2020/3/28 12:58
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Table( name = "scm.sys_bu_v")
@ApiModel(description = "事业部视图")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SysBuDO extends BaseEntity {
    @Column(name = "bu_code")
    @ApiModelProperty("事业部编码")
    private String bu_code;
    @Column(name = "bu_name")
    @ApiModelProperty("事业部名称")
    private String bu_name;

    public static SysBuEntity buildEntity(SysBuDO ruleWfDO) {
        SysBuEntity ruleWfEntity = new SysBuEntity();
        BeanUtils.copyProperties(ruleWfDO, ruleWfEntity);
        return ruleWfEntity;
    }

    public static SysBuDO buildDO(SysBuEntity ruleWfEntity) {
        SysBuDO ruleWfDO = new SysBuDO();
        BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
        return ruleWfDO;
    }

    public static List<SysBuEntity> batchBuildEntity(List<SysBuDO> headerDOList) {
        List<SysBuEntity> headerEntityList = new ArrayList<>();
        for (SysBuDO ruleWfDO : headerDOList) {
            SysBuEntity ruleWfEntity = buildEntity(ruleWfDO);
            headerEntityList.add(ruleWfEntity);
        }
        return headerEntityList;
    }

    public static List<SysBuDO> batchBuildDO(List<SysBuEntity> headerEntityList) {
        List<SysBuDO> headerDOList = new ArrayList<>();
        for (SysBuEntity ruleWfEntity : headerEntityList) {
            SysBuDO ruleWfDO = new SysBuDO();
            BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
            headerDOList.add(ruleWfDO);
        }
        return headerDOList;
    }
}
