package com.cvte.scm.wip.spi.rework.dto;

import com.cvte.csb.toolkit.StringUtils;
import com.cvte.scm.wip.domain.core.rework.entity.WipReworkBillLineEntity;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/8/7 15:21
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class EbsRwkBillLineCreateDTO {

    private String billLineId;

    private String billId;

    private String moId;

    private String moLotNo;

    private String moLotStatus;

    private String productModel;

    private Long moReworkQty;

    private String priProductNo;

    private String subProductNo;

    private String status;

    private String lRemark;

    private String rmk01;

    private String rmk02;

    private String rmk03;

    private String rmk04;

    private String rmk05;

    private String crtUser;

    private Date crtDate;

    private String updUser;

    private Date updDate;

    private String itemCode;

    public static List<EbsRwkBillLineCreateDTO> batchBuildDTO(List<WipReworkBillLineEntity> lineEntityList) {
        List<EbsRwkBillLineCreateDTO> ebsRwkBillLineCreateDTOList = new ArrayList<>();
        for (WipReworkBillLineEntity lineEntity : lineEntityList) {
            EbsRwkBillLineCreateDTO billLineCreateDTO = new EbsRwkBillLineCreateDTO();
            BeanUtils.copyProperties(lineEntity, billLineCreateDTO);
            billLineCreateDTO.setLRemark(lineEntity.getRemark());
            if (StringUtils.isBlank(lineEntity.getMoLotNo())) {
                // 生产批次为空的时候, 传递物料编码(01仓物料返工)
                billLineCreateDTO.setItemCode(lineEntity.getSubProductNo());
            }
            ebsRwkBillLineCreateDTOList.add(billLineCreateDTO);
        }
        return ebsRwkBillLineCreateDTOList;
    }

}
