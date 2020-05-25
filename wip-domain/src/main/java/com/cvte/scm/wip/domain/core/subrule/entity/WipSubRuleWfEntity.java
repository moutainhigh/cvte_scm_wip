package com.cvte.scm.wip.domain.core.subrule.entity;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * @author yt
 * @since 2020-02-24
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class WipSubRuleWfEntity {

    private String id;

    @ApiModelProperty(value = "审核流ID")
    private String ruleId;

    @ApiModelProperty(value = "节点,销管/PE工程师/...")
    private String node;

    @ApiModelProperty(value = "用户名称")
    private String userId;

    @ApiModelProperty(value = "${field.comment}")
    private String userName;

    private String crtUser;

    private Date crtTime;

    private String updUser;

    private Date updTime;

}