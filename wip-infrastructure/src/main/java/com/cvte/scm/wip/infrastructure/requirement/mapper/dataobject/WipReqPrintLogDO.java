package com.cvte.scm.wip.infrastructure.requirement.mapper.dataobject;


import com.cvte.scm.wip.domain.core.requirement.entity.WipReqPrintLogEntity;
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
import java.util.Date;

/**
 * @author jf
 * @since 2020-03-06
 */
@Data
@EqualsAndHashCode
@Accessors(chain = true)
@Table(name = "wip.wip_req_print_log")
@ApiModel(description = "投料单行打印日志记录表")
public class WipReqPrintLogDO {

    @Id
    @GeneratedValue(generator = "JDBC")
    @Column(name = "print_log_id")
    @ApiModelProperty(value = "${field.comment}")
    private String printLogId;
    /**
     * 投料单头ID
     */
    @Column(name = "header_id")
    @ApiModelProperty(value = "投料单头ID")
    private String headerId;
    /**
     * 查询字段的JSON字符串
     */
    @Column(name = "select_condition")
    @ApiModelProperty(value = "查询字段的JSON字符串")
    private String selectCondition;
    /**
     * 打印用户
     */
    @Column(name = "crt_user")
    @ApiModelProperty(value = "打印用户")
    private String crtUser;
    /**
     * 打印时间
     */
    @Column(name = "crt_time")
    @ApiModelProperty(value = "打印时间")
    private Date crtTime;

    public static WipReqPrintLogEntity buildEntity(WipReqPrintLogDO issuedDO) {
        WipReqPrintLogEntity issuedEntity = new WipReqPrintLogEntity();
        BeanUtils.copyProperties(issuedDO, issuedEntity);
        return issuedEntity;
    }

    public static WipReqPrintLogDO buildDO(WipReqPrintLogEntity issuedEntity) {
        WipReqPrintLogDO issuedDO = new WipReqPrintLogDO();
        BeanUtils.copyProperties(issuedEntity, issuedDO);
        return issuedDO;
    }

}