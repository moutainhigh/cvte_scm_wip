package com.cvte.scm.wip.domain.core.requirement.entity

import com.cvte.csb.core.exception.ServerException
import com.cvte.scm.wip.common.enums.AutoOperationIdentityEnum
import com.cvte.scm.wip.common.enums.StatusEnum
import com.cvte.scm.wip.common.enums.error.ReqInsErrEnum
import com.cvte.scm.wip.domain.BaseJunitTest
import com.cvte.scm.wip.domain.core.item.service.ScmItemService
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsDetailRepository
import com.cvte.scm.wip.domain.core.requirement.repository.ReqInsRepository
import com.cvte.scm.wip.domain.core.requirement.repository.WipLotRepository
import com.cvte.scm.wip.domain.core.requirement.valueobject.ReqInsBuildVO
import com.cvte.scm.wip.domain.core.requirement.valueobject.WipLotVO
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ChangedTypeEnum
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.InsOperationTypeEnum
import com.cvte.scm.wip.domain.core.requirement.valueobject.enums.ProcessingStatusEnum
import org.springframework.beans.factory.annotation.Autowired

import static org.mockito.Mockito.*

class ReqInsEntitySpec extends BaseJunitTest {

    @Autowired
    private WipLotRepository wipLotRepository
    @Autowired
    private ScmItemService scmItemService
    @Autowired
    private ReqInsRepository headerRepository
    @Autowired
    private ReqInsDetailRepository detailRepository

    def "新增未指定批次"() {
        given:
        ReqInsEntity insEntity = ReqInsEntity.get().setDetailList([
                ReqInsDetailEntity.get().setOperationType(InsOperationTypeEnum.ADD.code)
        ])
        when(wipLotRepository.selectByHeaderId(anyObject())).thenReturn([])
        when:
        insEntity.parse(null)
        then:
        ServerException se = thrown()
        se.status == ReqInsErrEnum.INVALID_INS.getCode()
    }

    def "新增工单批次"() {
        given:
        def aimReqLotNo = "lot1"
        def lotNo1 = "lot1_1"
        def lotNo2 = "lot1_2"
        ReqInsEntity insEntity = ReqInsEntity.get().setDetailList([
                ReqInsDetailEntity.get().setAimReqLotNo(aimReqLotNo).setMoLotNo(aimReqLotNo).setItemUnitQty(1.5).setOperationType(InsOperationTypeEnum.ADD.code)
        ])
        when(wipLotRepository.selectByHeaderId(anyObject())).thenReturn([
                new WipLotVO(lotNumber: lotNo1, lotQuantity: 100),
                new WipLotVO(lotNumber: lotNo2, lotQuantity: 150)
        ])
        when:
        def reqLineList = insEntity.parse(Collections.emptyMap())
        then:
        reqLineList.size() == 2
        and:
        def line1 = reqLineList.get(1)
        def line2 = reqLineList.get(0)
        line1.lotNumber == lotNo1 && line1.reqQty == 150
        line2.lotNumber == lotNo2 && line2.reqQty == 225
    }

    def "新增小批次"() {
        given:
        def aimReqLotNo = "lot1"
        def lotNo1 = "lot1_1"
        ReqInsEntity insEntity = ReqInsEntity.get().setAimReqLotNo(aimReqLotNo).setDetailList([
                ReqInsDetailEntity.get().setAimReqLotNo(aimReqLotNo).setMoLotNo(lotNo1).setItemUnitQty(1.5).setOperationType(InsOperationTypeEnum.ADD.code)
        ])
        when(wipLotRepository.selectByHeaderId(anyObject())).thenReturn([
                new WipLotVO(lotNumber: lotNo1, lotQuantity: 100)
        ])
        when:
        def reqLineList = insEntity.parse(Collections.emptyMap())
        then:
        def reqLine = reqLineList[0]
        reqLine.lotNumber == lotNo1 && reqLine.reqQty == 150
    }

    def "删除投料"() {
        given:
        ReqInsEntity insEntity = ReqInsEntity.get().setDetailList([
                ReqInsDetailEntity.get().setInsDetailId("d1").setOperationType(InsOperationTypeEnum.DELETE.code)
        ])
        def reqLineMap = ["d1": [new WipReqLineEntity()]]
        when:
        def reqLineList = insEntity.parse(reqLineMap)
        then:
        reqLineList[0].changeType == ChangedTypeEnum.DELETE.getCode()
    }

    def "替换投料"() {
        given:
        ReqInsEntity insEntity = ReqInsEntity.get().setDetailList([
                ReqInsDetailEntity.get().setInsDetailId("d1").setOperationType(InsOperationTypeEnum.REPLACE.code).setItemIdNew("111")
        ])
        def reqLineMap = ["d1": [new WipReqLineEntity(itemNo: "004.001")]]
        when(scmItemService.getItemNo(anyObject(), anyObject())).thenReturn("004.002")
        when:
        def reqLineList = insEntity.parse(reqLineMap)
        then:
        def reqLine = reqLineList[0]
        reqLine.changeType == ChangedTypeEnum.REPLACE.getCode() && reqLine.beforeItemNo == "004.001" && reqLine.itemNo == "004.002"
    }

    def "指令执行成功"() {
        given:
        ReqInsEntity reqIns = ReqInsEntity.get().setDetailList([
                ReqInsDetailEntity.get()
        ])
        doNothing().when(headerRepository).update(anyObject())
        doNothing().when(detailRepository).update(anyObject())
        when:
        reqIns.processSuccess()
        then:
        reqIns.status == ProcessingStatusEnum.SOLVED.code && reqIns.confirmedBy == AutoOperationIdentityEnum.WIP.code
        and:
        def detail = reqIns.detailList[0]
        detail.insStatus == ProcessingStatusEnum.SOLVED.code && detail.confirmedBy == AutoOperationIdentityEnum.WIP.code
    }

    def "作废指令"() {
        given:
        def reason = "test"
        def vo = new ReqInsBuildVO(insHeaderId: "h1", invalidReason: reason)
        def detailList = [ReqInsDetailEntity.get()]
        when(detailRepository.getByInsId(anyObject())).thenReturn(detailList)
        doNothing().when(detailRepository).update(anyObject())
        when:
        def ins = ReqInsEntity.get().deleteCompleteReqIns(vo)
        then:
        ins.status == StatusEnum.CLOSE.code && ins.invalidBy == AutoOperationIdentityEnum.WIP.code && ins.invalidReason == reason
        and:
        def detail = ins.detailList[0]
        detail.insStatus == StatusEnum.CLOSE.code && detail.invalidBy == AutoOperationIdentityEnum.WIP.code && detail.invalidReason == reason
    }

}
