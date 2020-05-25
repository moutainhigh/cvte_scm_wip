package com.cvte.scm.wip.domain.core.subrule.valueobject;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * @author : jf
 * Date    : 2020.02.28
 * Time    : 18:12
 * Email   ：jiangfeng7128@cvte.com
 */
@Data
public class WipSubRuleLotDetailVO {
    /* 组织 */
    private String organizationId;
    /* 生产批次号 */
    private String sourceLotNo;
    /* 产品编号 */
    private String productNo;
    /* 产品型号 */
    private String productModel;
    /* 产品描述 */
    private String productDes;
    /* 工单数量 */
    private Integer billQty;
    /* 工厂ID */
    private String factoryId;
    /* 工单发放状态 */
    private String wipStatusType;
    /* 齐套时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date mtrReadyTime;
    /* 上线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date planStartDate;
}