package com.cvte.scm.wip.spi.rtc.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/9/30 14:50
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class XxwipTransactionHeadersDTO {

    private String interfaceOrigSource;

    private String interfaceOrigSourceId;

    private String interfaceAction;

    private String organizationName;

    private String organizationId;

    private String transactionNumber;

    private String applyDate;

    private String transactionDate;

    private String transactionTypeDesc;

    private String subinventoryCode;

    private String locator;

    private String wipEntityName;

    private String wipLotNumber;

    private String startQuantity;

    private String remark;

    private String operationSeqNum;

    private String departmentCode;

    private String rejectReason;

    private String batchId;

    private String userName;

    private String autoDist;

    private String submitFlag;

    private String postFlag;

    private List<XxwipTransactionLinesDTO> importLnJson;

    @Data
    @Accessors(chain = true)
    public static class XxwipTransactionLinesDTO {

        private String interfaceOrigSource;

        private String interfaceOrigSourceId;

        private String organizationName;

        private String organizationId;

        private String transactionNumber;

        private String intfHeaderId;

        private String componentItem;

        private String operationSeqNum;

        private String supplySubinventory;

        private String supplyLocator;

        private String quantityIssue;

        private String notes;

        private String vendorName;

        private String badReason;

        private String batchId;

        private String userName;

        private String seqNum;

        private String badDesc;

        private List<XxwipTransactionAssignsDTO> importBatchJson;

    }

    @Data
    @Accessors(chain = true)
    public static class XxwipTransactionAssignsDTO {

            private String interfaceOrigSource;

            private String interfaceOrigSourceId;

            private String organizationName;

            private String organizationId;

            private String componentItem;

            private String operationSeqNum;

            private String lotNumber;

            private String transactionDate;

            private String expirationDate;

            private String transactionQuantity;

            private String intfTransHeaderId;

            private String intfTransLineId;

            private String batchId;

    }

}
