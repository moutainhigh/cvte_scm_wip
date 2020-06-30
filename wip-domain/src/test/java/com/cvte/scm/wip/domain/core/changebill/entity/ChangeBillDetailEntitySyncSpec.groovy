package com.cvte.scm.wip.domain.core.changebill.entity

import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.changebill.repository.ChangeBillDetailRepository
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillBuildVO
import com.cvte.scm.wip.domain.core.changebill.valueobject.ChangeBillDetailBuildVO
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired

class ChangeBillDetailEntitySyncSpec extends BaseJunitTest {

    @Autowired
    private ChangeBillDetailRepository changeBillDetailRepository;

    def "工厂确认更新单据"() {
        given:
        def billId = "f732bfe05c06403c9a3916b6468ee086"
        def sourceLineId = "3538156968"
        def posNoList = [null, "RB323", "RB835"]
        def detailIdList = ["d1", "d2", "d3"]
        def vo = new ChangeBillBuildVO(
             billId: billId,
             detailVOList: [
                     new ChangeBillDetailBuildVO(sourceLineId: sourceLineId, posNo: posNoList[0]),
                     new ChangeBillDetailBuildVO(sourceLineId: sourceLineId, posNo: posNoList[1]),
                     new ChangeBillDetailBuildVO(sourceLineId: sourceLineId, posNo: posNoList[2]),
             ]
        )
        def detailEntityList = [
                ChangeBillDetailEntity.get().setSourceLineId(sourceLineId).setPosNo(posNoList[0]).setDetailId(detailIdList[0]),
                ChangeBillDetailEntity.get().setSourceLineId(sourceLineId).setPosNo(posNoList[1]).setDetailId(detailIdList[1]),
                ChangeBillDetailEntity.get().setSourceLineId(sourceLineId).setPosNo(posNoList[2]).setDetailId(detailIdList[2]),
        ]
        Mockito.when(changeBillDetailRepository.selectByBillId(billId)).thenReturn(detailEntityList)
        when:
        def resultList = ChangeBillDetailEntity.get().batchSaveDetail(vo, true)
        then:
        resultList.size() == 3
        resultList[0].detailId == detailIdList[0] && resultList[1].detailId == detailIdList[1] && resultList[2].detailId == detailIdList[2]
    }

}
