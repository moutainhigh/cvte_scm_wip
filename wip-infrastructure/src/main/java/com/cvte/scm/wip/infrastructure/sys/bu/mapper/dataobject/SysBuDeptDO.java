package com.cvte.scm.wip.infrastructure.sys.bu.mapper.dataobject;

import com.cvte.csb.validator.entity.BaseEntity;
import com.cvte.scm.wip.domain.common.bu.entity.SysBuDeptEntity;
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
@Table( name = "scm.sys_bu_dept_v")
@ApiModel(description = "部门视图")
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
public class SysBuDeptDO extends BaseEntity {
    @Column(name = "dept_name")
    @ApiModelProperty("部门编码")
    private String deptName;
    @Column(name = "dept_code")
    @ApiModelProperty("部门名称")
    private String deptCode;
    @Column(name = "bu_code")
    @ApiModelProperty("事业部编码")
    private String buCode;

    public static SysBuDeptEntity buildEntity(SysBuDeptDO ruleWfDO) {
        SysBuDeptEntity ruleWfEntity = new SysBuDeptEntity();
        BeanUtils.copyProperties(ruleWfDO, ruleWfEntity);
        return ruleWfEntity;
    }

    public static SysBuDeptDO buildDO(SysBuDeptEntity ruleWfEntity) {
        SysBuDeptDO ruleWfDO = new SysBuDeptDO();
        BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
        return ruleWfDO;
    }

    public static List<SysBuDeptEntity> batchBuildEntity(List<SysBuDeptDO> headerDOList) {
        List<SysBuDeptEntity> headerEntityList = new ArrayList<>();
        for (SysBuDeptDO ruleWfDO : headerDOList) {
            SysBuDeptEntity ruleWfEntity = buildEntity(ruleWfDO);
            headerEntityList.add(ruleWfEntity);
        }
        return headerEntityList;
    }

    public static List<SysBuDeptDO> batchBuildDO(List<SysBuDeptEntity> headerEntityList) {
        List<SysBuDeptDO> headerDOList = new ArrayList<>();
        for (SysBuDeptEntity ruleWfEntity : headerEntityList) {
            SysBuDeptDO ruleWfDO = new SysBuDeptDO();
            BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
            headerDOList.add(ruleWfDO);
        }
        return headerDOList;
    }
}
