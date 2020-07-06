package com.cvte.scm.wip.domain.core.changebill.valueobject;

import com.cvte.scm.wip.common.base.domain.VO;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillDetailEntity;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.beans.BeanUtils;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/5/19 14:22
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Data
@Accessors(chain = true)
public class ChangeBillBuildVO implements VO {

    private String billId;

    private String billNo;

    private String organizationId;

    private String billType;

    private String moId;

    private String billStatus;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date enableDate;

    private Date disableDate;

    private Date lastUpdDate;

    private String changeContent;
    
    private String remarks;

    private String pcRemarks;

    private String motLotNo;

    private String sourceNo;

    private String toMoLotNo;

    private List<ChangeBillDetailBuildVO> detailVOList;

    public static ChangeBillBuildVO completeBuild(ChangeBillEntity bill) {
        ChangeBillBuildVO billBuildVO = new ChangeBillBuildVO();
        BeanUtils.copyProperties(bill, billBuildVO);
        List<ChangeBillDetailBuildVO> detailList = new ArrayList<>();
        for (ChangeBillDetailEntity detail : bill.getBillDetailList()) {
            ChangeBillDetailBuildVO detailBuildVO = new ChangeBillDetailBuildVO();
            BeanUtils.copyProperties(detail, detailBuildVO);
            // 更新生效、失效时间
            detailBuildVO.setEnableDate(bill.getEnableDate())
                    .setDisableDate(bill.getDisableDate());
            detailList.add(detailBuildVO);
        }
        billBuildVO.setDetailVOList(detailList);
        return billBuildVO;
    }

}
