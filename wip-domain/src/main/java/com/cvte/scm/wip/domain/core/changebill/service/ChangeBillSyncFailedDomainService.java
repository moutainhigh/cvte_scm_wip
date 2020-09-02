package com.cvte.scm.wip.domain.core.changebill.service;

import com.cvte.scm.wip.common.base.domain.DomainService;
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum;
import com.cvte.scm.wip.domain.core.changebill.entity.ChangeBillEntity;
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillRepository;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO;
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillQueryVO;
import org.springframework.stereotype.Service;

import java.util.*;

/**
  * 
  * @author  : xueyuting
  * @since    : 2020/6/17 20:46
  * @version : 1.0
  * email   : xueyuting@cvte.com
  */
@Service
public class ChangeBillSyncFailedDomainService implements DomainService {

    private ChangeBillRepository changeBillRepository;

    public ChangeBillSyncFailedDomainService(ChangeBillRepository changeBillRepository) {
        this.changeBillRepository = changeBillRepository;
    }

    public List<ChangeBillBuildVO> addSyncFailedBills(List<ChangeBillBuildVO> sourceBillBuildVOList, ChangeBillQueryVO queryVO) {
        // 新同步过来的更改单
        Map<String, Boolean> newSyncBillMap = new HashMap<>();
        sourceBillBuildVOList.forEach(vo -> newSyncBillMap.put(vo.getBillNo(), true));

        List<String> errMsgList = new ArrayList<>();
        errMsgList.add(ReqInsErrEnum.TARGET_REQ_INVALID.getDesc());
        List<ChangeBillEntity> billList = changeBillRepository.getSyncFailedBills(errMsgList, queryVO);

        // 创建指令失败的更改单列表
        List<ChangeBillBuildVO> billBuildVOList = new ArrayList<>(sourceBillBuildVOList);
        for (ChangeBillEntity bill : billList) {
            Boolean newSyncBill = newSyncBillMap.get(bill.getBillNo());
            if (Objects.nonNull(newSyncBill) && newSyncBill) {
                // 同步过来的单据中已有的不重复添加
                continue;
            }
            ChangeBillBuildVO billBuildVO = ChangeBillBuildVO.completeBuild(bill);
            billBuildVOList.add(billBuildVO);
        }
        return billBuildVOList;
    }

}
