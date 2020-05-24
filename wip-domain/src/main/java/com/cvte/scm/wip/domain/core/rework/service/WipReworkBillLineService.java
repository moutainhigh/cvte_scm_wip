package com.cvte.scm.wip.domain.core.rework.service;

import com.cvte.csb.wfp.api.sdk.util.ListUtil;
import com.cvte.scm.wip.domain.core.rework.repository.WipReworkBillLineRepository;
import com.cvte.scm.wip.domain.core.rework.valueobject.WipRwkBillLVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务实现类
 *
 * @author author
 * @since 2020-03-23
 */
@Service
@Transactional
public class WipReworkBillLineService {

    private WipReworkBillLineRepository wipReworkBillLineRepository;

    public WipReworkBillLineService(WipReworkBillLineRepository wipReworkBillLineRepository) {
        this.wipReworkBillLineRepository = wipReworkBillLineRepository;
    }

    public List<WipRwkBillLVO>  sumRwkQty(List<String> factoryIdList, List<String> moLotNo) {
        if (ListUtil.empty(factoryIdList) || ListUtil.empty(moLotNo)) {
            return null;
        }
        return wipReworkBillLineRepository.sumRwkQty(factoryIdList, moLotNo);
    }
}
