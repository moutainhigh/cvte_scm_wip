package com.cvte.scm.wip.spi.rework.dto;

import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillLineEntity;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/4/9 09:25
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
@ApiModel(value = "创建EBS返工单的数据传输对象, 字段注释参考WipRwkBillH")
public class EbsRwkBillCreateDTO {

    private String billId;

    private String billNo;

    private String organizationId;

    private String factoryId;

    private String reworkType;

    private String reworkReasonType;

    private String reworkReason;

    private String reworkDesc;

    private String respBu;

    private String respDept;

    private String expectFinishDate;

    private String expectDeliveryDate;

    private String billStatus;

    private String sourceSoNo;

    private String rejectDealType;

    private String goodDealType;

    private String rejectMtrDealType;

    private String goodMtrDealType;

    private String rejectDealOtherType;

    private String remark;

    private String userNo;

    private List<EbsRwkBillLineCreateDTO> importLnJson;

    private String faultMaker;

    private String firstDiscoverer;

    private String topManagement;

    private String changeCode;

    private String customerName;
}
