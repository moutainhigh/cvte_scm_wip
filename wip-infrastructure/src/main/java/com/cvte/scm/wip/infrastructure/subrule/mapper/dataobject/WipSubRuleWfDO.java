package com.cvte.scm.wip.infrastructure.subrule.mapper.dataobject;


import com.cvte.scm.wip.domain.core.subrule.entity.WipSubRuleWfEntity;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author yt
 * @since 2020-02-24
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Table(name = "wip.wip_sub_rule_wf")
@ApiModel(description = "审核流节点信息")
public class WipSubRuleWfDO {

    private static final long serialVersionUID = 1L;

    /**
     * ${field.comment}
     */
    @Id
    @Column(name = "id")
    @GeneratedValue(generator = "JDBC")
    @ApiModelProperty(value = "${field.comment}")
    private String id;
    /**
     * 审核流ID
     */
    @Column(name = "rule_id")
    @ApiModelProperty(value = "审核流ID")
    private String ruleId;
    /**
     * 节点,销管/PE工程师/...
     */
    @Column(name = "node")
    @ApiModelProperty(value = "节点,销管/PE工程师/...")
    private String node;
    /**
     * 用户名称
     */
    @Column(name = "user_id")
    @ApiModelProperty(value = "用户名称")
    private String userId;
    /**
     * ${field.comment}
     */
    @Column(name = "user_name")
    @ApiModelProperty(value = "${field.comment}")
    private String userName;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "${field.comment}")
    private String crtUser;
    /**
     * ${field.comment}
     */
    @Column(name = "crt_time")
    @ApiModelProperty(value = "${field.comment}")
    private Date crtTime;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_user")
    @ApiModelProperty(value = "${field.comment}")
    private String updUser;
    /**
     * ${field.comment}
     */
    @Column(name = "upd_time")
    @ApiModelProperty(value = "${field.comment}")
    private Date updTime;

    public static WipSubRuleWfEntity buildEntity(WipSubRuleWfDO ruleWfDO) {
        WipSubRuleWfEntity ruleWfEntity = new WipSubRuleWfEntity();
        BeanUtils.copyProperties(ruleWfDO, ruleWfEntity);
        return ruleWfEntity;
    }

    public static WipSubRuleWfDO buildDO(WipSubRuleWfEntity ruleWfEntity) {
        WipSubRuleWfDO ruleWfDO = new WipSubRuleWfDO();
        BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
        return ruleWfDO;
    }

    public static List<WipSubRuleWfEntity> batchBuildEntity(List<WipSubRuleWfDO> headerDOList) {
        List<WipSubRuleWfEntity> headerEntityList = new ArrayList<>();
        for (WipSubRuleWfDO ruleWfDO : headerDOList) {
            WipSubRuleWfEntity ruleWfEntity = buildEntity(ruleWfDO);
            headerEntityList.add(ruleWfEntity);
        }
        return headerEntityList;
    }

    public static List<WipSubRuleWfDO> batchBuildDO(List<WipSubRuleWfEntity> headerEntityList) {
        List<WipSubRuleWfDO> headerDOList = new ArrayList<>();
        for (WipSubRuleWfEntity ruleWfEntity : headerEntityList) {
            WipSubRuleWfDO ruleWfDO = new WipSubRuleWfDO();
            BeanUtils.copyProperties(ruleWfEntity, ruleWfDO);
            headerDOList.add(ruleWfDO);
        }
        return headerDOList;
    }

}