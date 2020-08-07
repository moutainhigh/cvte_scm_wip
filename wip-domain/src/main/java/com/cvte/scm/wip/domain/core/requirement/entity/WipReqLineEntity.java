package com.cvte.scm.wip.domain.core.requirement.entity;


import com.cvte.csb.toolkit.ObjectUtils;
import com.cvte.scm.wip.common.utils.CodeableEnumUtils;
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.BillStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Transient;
import javax.validation.constraints.DecimalMin;
import java.util.*;

/**
 * @author jf
 * @since 2019-12-30
 */
@Data
@Accessors(chain = true)
public class WipReqLineEntity {

    private String lineId;

    @ApiModelProperty(value = "投料单头ID，来源于wip_req_header表。")
    private String headerId;

    private String organizationId;

    @ApiModelProperty(value = "工序号")
    private String wkpNo;

    @ApiModelProperty(value = "工序号，替换工序号时使用")
    private String beforeWkpNo;

    @ApiModelProperty(value = "批次号")
    private String lotNumber;

    @ApiModelProperty(value = "位号")
    private String posNo;

    @ApiModelProperty(value = "物料编码")
    private String itemNo;

    @ApiModelProperty(value = "物料编码")
    private String beforeItemNo;

    @ApiModelProperty(value = "物料ID")
    private String itemId;

    @ApiModelProperty(value = "单位用量")
    @DecimalMin(value = "0.0", message = "单位用量必须大于等于零")
    private Double unitQty;

    @ApiModelProperty(value = "需求数量")
    @DecimalMin(value = "0", message = "需求数量必须大于等于零")
    private Integer reqQty;

    @ApiModelProperty(value = "已发料数量")
    @DecimalMin(value = "0", message = "已发料数量必须大于等于零")
    private Integer issuedQty;

    @ApiModelProperty(value = "超发原因")
    private String exceedReason;

    @ApiModelProperty(value = "原始行ID")
    private String originalLineId;

    @ApiModelProperty(value = "变更前行ID")
    private String beforeLineId;

    @ApiModelProperty(value = "行状态")
    private String lineStatus;

    @ApiModelProperty(value = "变更原因")
    private String changeReason;

    @ApiModelProperty(value = "行版本，主要为了标记被取消数据，如果数据行被取消，则将行ID记录其中，否则为空")
    private String lineVersion;

    @ApiModelProperty(value = "来源编号")
    private String sourceCode = "SCM-WIP";

    @ApiModelProperty(value = "生效时间")
    private Date enableDate;

    @ApiModelProperty(value = "失效时间")
    private Date disableDate;


    private String rmk01;

    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;

    private String rmk06;

    private String rmk07;

    private String rmk08;

    private String rmk09;

    private String rmk10;

    private String rmk11;

    private String rmk12;

    private String rmk13;

    private String rmk14;

    private String rmk15;

    private String crtUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date crtDate;

    private String updUser;

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updDate;

    @Transient
    private String groupId;

    // 变更类型
    @Transient
    private String changeType;

    // 指令ID
    @Transient
    private String insDetailId;

    // 变更结果
    @Transient
    private String executeResult;

    public boolean isProcessing() {
        BillStatusEnum billStatusEnum = CodeableEnumUtils.getCodeableEnumByCode(this.getLineStatus(), BillStatusEnum.class);
        return ObjectUtils.isNotNull(billStatusEnum)
                && Arrays.asList(BillStatusEnum.DRAFT, BillStatusEnum.CONFIRMED, BillStatusEnum.PREPARED).contains(billStatusEnum);
    }

    @Override
    public boolean equals(Object obj) {
        if (Objects.nonNull(obj) && obj instanceof WipReqLineEntity) {
            WipReqLineEntity wipReqLine = (WipReqLineEntity) obj;
            return Objects.equals(this.headerId, wipReqLine.headerId) &&
                    Objects.equals(this.organizationId, wipReqLine.organizationId) &&
                    Objects.equals(this.lotNumber, wipReqLine.lotNumber) &&
                    Objects.equals(this.wkpNo, wipReqLine.wkpNo) &&
                    Objects.equals(this.posNo, wipReqLine.posNo) &&
                    Objects.equals(this.itemId, wipReqLine.itemId) &&
                    Objects.equals(this.itemNo, wipReqLine.itemNo) &&
                    Objects.equals(this.lineVersion, wipReqLine.lineVersion) &&
                    // 增加变更类型, add by tingyx 20-06-20
                    Objects.equals(this.changeType, wipReqLine.changeType);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Objects.hashCode(this.headerId);
        result = 31 * result + Objects.hashCode(this.organizationId);
        result = 31 * result + Objects.hashCode(this.lotNumber);
        result = 31 * result + Objects.hashCode(this.wkpNo);
        result = 31 * result + Objects.hashCode(this.posNo);
        result = 31 * result + Objects.hashCode(this.itemId);
        result = 31 * result + Objects.hashCode(this.itemNo);
        result = 31 * result + Objects.hashCode(this.lineVersion);
        // 增加变更类型
        result = 31 * result + Objects.hashCode(this.changeType);
        return result;
    }

    /**
     * 挑选出除了物料ID和物料编码外其他主键均相同的投料行
     * @since 2020/8/6 5:44 下午
     * @author xueyuting
     * @return 被挑选出来的投料行
     */
    public static List<WipReqLineEntity> retrieveDuplicateExceptItem(List<WipReqLineEntity> lineList) {
        Map<WipReqLineEntity, List<WipReqLineEntity>> lineMap = new HashMap<>();
        for (WipReqLineEntity line : lineList) {
            WipReqLineEntity keyLine = new WipReqLineEntity();
            BeanUtils.copyProperties(line, keyLine);
            keyLine.setItemId(null).setItemNo(null);
            // 把改后主键相同的投料行放在集合中
            List<WipReqLineEntity> dupLineList = lineMap.computeIfAbsent(keyLine, k -> new ArrayList<>());
            dupLineList.add(line);
        }

        List<WipReqLineEntity> resultList = new ArrayList<>();
        for (List<WipReqLineEntity> dupLineList : lineMap.values()) {
            if (dupLineList.size() > 1) {
                resultList.addAll(dupLineList);
            }
        }
        return resultList;
    }

}