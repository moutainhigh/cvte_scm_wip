package com.cvte.scm.wip.domain.core.requirement.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.Objects;

/**
 * WIP工单接口表
 *
 * @author jf
 * @since 2019-02-05
 */
@Data
@Accessors(chain = true)
public class XxwipMoInterfaceEntity {
    private String interfaceId;

    @ApiModelProperty(value = "组织ID")
    private String organizationId;

    @ApiModelProperty(value = "群ID")
    private String groupId;

    @ApiModelProperty(value = "工单实体ID")
    private String wipEntityId;

    @ApiModelProperty(value = "大批次号")
    private String lotNumber;

    @ApiModelProperty(value = "物料ID")
    private String itemId;

    @ApiModelProperty(value = "物料数量")
    private Long itemQty;

    @ApiModelProperty(value = "工序号")
    private String wkpNo;

    @ApiModelProperty(value = "位号")
    private String posNo;

    @ApiModelProperty(value = "操作类型：add、delete、update和replace")
    private String operationType;

    @ApiModelProperty(value = "执行编号")
    private String executeCode;

    private String crtUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date crtTime;

    private String updUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updTime;

    @Override
    public boolean equals(Object obj) {
        if (Objects.nonNull(obj) && obj instanceof XxwipMoInterfaceEntity) {
            XxwipMoInterfaceEntity xxwipMoInterfaceEntity = (XxwipMoInterfaceEntity) obj;
            return Objects.equals(this.groupId, xxwipMoInterfaceEntity.groupId) &&
                    Objects.equals(this.interfaceId, xxwipMoInterfaceEntity.interfaceId);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.interfaceId);
        result = 31 * result + Objects.hashCode(this.groupId);
        return result;
    }
}